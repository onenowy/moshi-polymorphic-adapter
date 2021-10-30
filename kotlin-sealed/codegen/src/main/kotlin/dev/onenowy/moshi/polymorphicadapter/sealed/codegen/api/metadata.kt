package dev.onenowy.moshi.polymorphicadapter.sealed.codegen.api

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.metadata.isInternal
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import dev.onenowy.moshi.polymorphicadapter.*
import dev.onenowy.moshi.polymorphicadapter.sealed.codegen.KotlinSealedCodegenProcessor.Companion.COMMON_SUPPRESS
import dev.onenowy.moshi.polymorphicadapter.sealed.codegen.KotlinSealedCodegenProcessor.Companion.defaultNullAnnotation
import dev.onenowy.moshi.polymorphicadapter.sealed.codegen.KotlinSealedCodegenProcessor.Companion.moshiClass
import kotlinx.metadata.KmClass
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

internal fun adapterPropertySpec(
    generatorTag: List<String>,
    element: TypeElement,
    sealedSubClasses: List<TypeElement>,
    targetType: ClassName,
    moshiParam: ParameterSpec,
    jsonAdapterType: ParameterizedTypeName,
    allocator: NameAllocator,
    messager: Messager
): PropertySpec? {
    val adapterPropertyInit = if (generatorTag[0] == PolymorphicAdapterType.NAME_POLYMORPHIC_ADAPTER) {
        nameAdapterInitializer(element, sealedSubClasses).toBuilder()
    } else {
        valueAdapterInitializer(generatorTag, element, sealedSubClasses, messager)?.toBuilder() ?: return null
    }

    if (element.getAnnotation(defaultNullAnnotation) != null) {
        adapterPropertyInit.add(".withDefaultValue(%L)\n", null)
    }

    adapterPropertyInit.add(
        "  .create(%T::class.java, %M(), %N) as %T\n",
        targetType,
        MemberName("kotlin.collections", "emptySet"),
        moshiParam,
        jsonAdapterType
    )
    return PropertySpec.builder(allocator.newName("polymorphicAdapter"), jsonAdapterType, KModifier.PRIVATE)
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
    val polymorphicAdapterProperty = adapterPropertySpec(
        generatorTag,
        element,
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
    classBuilder.addProperty(polymorphicAdapterProperty)
        .addFunction(
            FunSpec.builder("fromJson")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(readerParam)
                .returns(nullableTargetType)
                .addStatement("return %N.fromJson(%N)", polymorphicAdapterProperty, readerParam)
                .build()
        )
        .addFunction(
            FunSpec.builder("toJson")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(writerParam)
                .addParameter(valueParam)
                .addStatement("%N.toJson(%N, %N)", polymorphicAdapterProperty, writerParam, valueParam)
                .build()
        )
    return classBuilder.build()
}

@OptIn(DelicateKotlinPoetApi::class)
private fun nameAdapterInitializer(baseType: TypeElement, subClasses: List<TypeElement>): CodeBlock {
    return buildCodeBlock {
        add(
            " %T.of(%T::class.java)\n",
            NamePolymorphicAdapterFactory::class,
            baseType.asClassName()
        )
        for (type in subClasses) {
            val nameLabel =
                type.getAnnotation(dev.onenowy.moshi.polymorphicadapter.sealed.codegen.KotlinSealedCodegenProcessor.nameLabelAnnotation)
            add(
                ".withSubtype(%T::class.java, %S)\n",
                type.asClassName(),
                nameLabel.name
            )
        }
    }
}

@OptIn(DelicateKotlinPoetApi::class)
private fun valueAdapterInitializer(
    generatorTag: List<String>,
    baseType: TypeElement,
    subClasses: List<TypeElement>,
    messager: Messager
): CodeBlock? {
    val labelType = getSupportedTypeClass(generatorTag[0])
    return buildCodeBlock {
        add(
            "%T.of(%T::class.java, %S, %T::class.java)\n",
            ValuePolymorphicAdapterFactory::class,
            baseType.asClassName(),
            generatorTag[1],
            labelType
        )
        for (type in subClasses) {
            val labelValue =
                type.getAnnotation(dev.onenowy.moshi.polymorphicadapter.sealed.codegen.KotlinSealedCodegenProcessor.valueLabelAnnotation)
            val value = labelValue.value.toSupportedTypeValueOrNull(generatorTag[0])
            if (value == null) {
                messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "${labelValue.value} cannot be cast to ${getSupportedTypeClass(generatorTag[1]).simpleName}"
                )
                return null
            }
            add(
                ".withSubtype(%T::class.java, %L)\n",
                type.asClassName(),
                value
            )
        }
    }
}