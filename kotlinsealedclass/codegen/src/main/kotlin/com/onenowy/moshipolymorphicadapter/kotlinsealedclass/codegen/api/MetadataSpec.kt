package com.onenowy.moshipolymorphicadapter.kotlinsealedclass.codegen.api

import com.onenowy.moshipolymorphicadapter.*
import com.onenowy.moshipolymorphicadapter.kotlinsealedclass.codegen.KotlinSealedCodegenProcessor
import com.onenowy.moshipolymorphicadapter.kotlinsealedclass.codegen.KotlinSealedCodegenProcessor.Companion.moshiClass
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.metadata.isInternal
import com.squareup.kotlinpoet.metadata.toKmClass
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import kotlinx.metadata.KmClass
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

internal fun adapterPropertySpec(
    generatorTag: List<String>,
    element: TypeElement,
    kmClass: KmClass,
    sealedSubClasses: List<TypeElement>,
    targetType: ClassName,
    moshiParam: ParameterSpec,
    jsonAdapterType: ParameterizedTypeName,
    allocator: NameAllocator,
    messager: Messager
): PropertySpec? {
    val adapterPropertyInit = if (generatorTag[0] == AdapterType.NAME_ADAPTER) {
        nameAdapterInitializer(kmClass, sealedSubClasses).toBuilder()
    } else {
        valueAdapterInitializer(generatorTag, kmClass, sealedSubClasses, messager)?.toBuilder() ?: return null
    }

    if (element.getAnnotation(KotlinSealedCodegenProcessor.defaultNullAnnotation) != null) {
        adapterPropertyInit.add(".withDefaultValue(%L)\n", null)
    }

    adapterPropertyInit.add(
        "  .create(%T::class.java, %M(), %N) as %T\n",
        targetType,
        MemberName("kotlin.collections", "emptySet"),
        moshiParam,
        jsonAdapterType
    )
    return PropertySpec.builder(allocator.newName("runtimeAdapter"), jsonAdapterType, KModifier.PRIVATE)
        .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "UNCHECKED_CAST").build())
        .initializer(adapterPropertyInit.build()).build()
}

internal fun adapterClassSpec(
    element: TypeElement,
    kmClass: KmClass,
    adapterName: String,
    targetType: ClassName,
    generatorTag: List<String>,
    sealedSubClasses: List<TypeElement>,
    messager: Messager,
    generatedAnnotation: ClassName?
): TypeSpec? {
    val COMMON_SUPPRESS = arrayOf(
        // https://github.com/square/moshi/issues/1023
        "DEPRECATION",
        // Because we look it up reflectively
        "unused",
        // Because we include underscores
        "ClassName",
        // Because we generate redundant `out` variance for some generics and there's no way
        // for us to know when it's redundant.
        "REDUNDANT_PROJECTION",
        // Because we may generate redundant explicit types for local vars with default values.
        // Example: 'var fooSet: Boolean = false'
        "RedundantExplicitType",
        // NameAllocator will just add underscores to differentiate names, which Kotlin doesn't
        // like for stylistic reasons.
        "LocalVariableName",
        // KotlinPoet always generates explicit public modifiers for public members.
        "RedundantVisibilityModifier"
    ).let { suppression ->
        AnnotationSpec.builder(Suppress::class)
            .addMember(
                suppression.indices.joinToString { "%S" },
                *suppression
            )
            .build()
    }

    val allocator = NameAllocator()
    val visibilityModifier = if (kmClass.flags.isInternal) KModifier.INTERNAL else KModifier.PUBLIC
    val moshiParam = ParameterSpec.builder(allocator.newName("moshi"), moshiClass).build()
    val jsonAdapterType = JsonAdapter::class.asClassName().parameterizedBy(targetType)
    val primaryConstructor = FunSpec.constructorBuilder().addParameter(moshiParam).build()

    val classBuilder =
        TypeSpec.classBuilder(adapterName).addAnnotation(COMMON_SUPPRESS).addModifiers(visibilityModifier)
            .superclass(jsonAdapterType).primaryConstructor(primaryConstructor).addOriginatingElement(element)

    generatedAnnotation?.let {
        classBuilder.addAnnotation(it)
    }
    val runtimeAdapterProperty = adapterPropertySpec(
        generatorTag,
        element,
        kmClass,
        sealedSubClasses,
        targetType,
        moshiParam,
        jsonAdapterType,
        allocator,
        messager
    ) ?: return null
    val nullableTargetType = targetType.copy(nullable = true)
    val readerParam = ParameterSpec(allocator.newName("reader"), JsonReader::class.asClassName())
    val writerParam = ParameterSpec(allocator.newName("writer"), JsonWriter::class.asClassName())
    val valueParam = ParameterSpec(allocator.newName("value"), nullableTargetType)
    classBuilder.addProperty(runtimeAdapterProperty)
        .addFunction(
            FunSpec.builder("fromJson")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(readerParam)
                .returns(nullableTargetType)
                .addStatement("return %N.fromJson(%N)", runtimeAdapterProperty, readerParam)
                .build()
        )
        .addFunction(
            FunSpec.builder("toJson")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(writerParam)
                .addParameter(valueParam)
                .addStatement("%N.toJson(%N, %N)", runtimeAdapterProperty, writerParam, valueParam)
                .build()
        )
    return classBuilder.build()
}

private fun nameAdapterInitializer(baseType: KmClass, subClasses: List<TypeElement>): CodeBlock {
    return buildCodeBlock {
        add(
            " %T.of(%T::class.java)\n",
            NamePolymorphicAdapterFactory::class,
            baseType.toClassName()
        )
        for (type in subClasses) {
            val nameLabel = type.getAnnotation(KotlinSealedCodegenProcessor.nameLabelAnnotation)
            add(
                ".withSubtype(%T::class.java, %S)\n",
                type.toKmClass().toClassName(),
                nameLabel.name
            )
        }
    }
}

private fun valueAdapterInitializer(
    generatorTag: List<String>,
    baseType: KmClass,
    subClasses: List<TypeElement>,
    messager: Messager
): CodeBlock? {
    val labelType = getSupportTypeClass(generatorTag[0])
    return buildCodeBlock {
        add(
            "%T.of(%T::class.java, %S, %T::class.java)\n",
            ValuePolymorphicAdapterFactory::class,
            baseType.toClassName(),
            generatorTag[1],
            labelType
        )
        for (type in subClasses) {
            val labelValue = type.getAnnotation(KotlinSealedCodegenProcessor.valueLabelAnnotation)
            val value = labelValue.value.toSupportTypeOrNull(generatorTag[0])
            if (value == null) {
                messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "${labelValue.value} cannot be cast to ${getSupportTypeClass(generatorTag[1]).simpleName}"
                )
                return null
            }
            add(
                ".withSubtype(%T::class.java, %L)\n",
                type.toKmClass().toClassName(),
                value
            )
        }
    }
}


