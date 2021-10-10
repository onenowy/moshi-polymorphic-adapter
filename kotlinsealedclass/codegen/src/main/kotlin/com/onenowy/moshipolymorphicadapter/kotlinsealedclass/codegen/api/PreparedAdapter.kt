package com.onenowy.moshipolymorphicadapter.kotlinsealedclass.codegen.api

import com.onenowy.moshipolymorphicadapter.kotlinsealedclass.codegen.KotlinSealedCodegenProcessor
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.asClassName
import kotlinx.metadata.KmClass
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

internal data class PreparedAdapter(val fileSpec: FileSpec, val proguardRules: ProguardRules?)

@OptIn(DelicateKotlinPoetApi::class)
internal fun preparedAdapter(
    element: TypeElement,
    kmClass: KmClass,
    sealedSubClasses: List<TypeElement>,
    generatorTag: List<String>,
    messager: Messager,
    generatedAnnotation: ClassName?,
    generateProguardRules: Boolean
): PreparedAdapter? {

    for (subclass in sealedSubClasses) {
        if (subclass.typeParameters.isNotEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Moshi-sealed subtypes cannot be generic.", subclass)
            return null
        }
    }
    val adapterName = ClassName.bestGuess(
        com.squareup.moshi.Types.generatedJsonAdapterName(
            element.asClassName().reflectionName()
        )
    ).simpleName
    val targetType = element.asClassName()
    val typeSpec = adapterClassSpec(
        element,
        kmClass,
        adapterName,
        targetType,
        generatorTag,
        sealedSubClasses,
        messager,
        generatedAnnotation
    )
    typeSpec ?: return null

    val fileSpec = FileSpec.builder(targetType.packageName, adapterName)
        .addType(typeSpec)
        .build()

    val proguardRules = if (generateProguardRules) {
        ProguardRules(
            targetClass = targetType,
            adapterName = adapterName,
            adapterConstructorParams = listOf(KotlinSealedCodegenProcessor.moshiClass.asClassName().reflectionName())
        )
    } else {
        null
    }

    return PreparedAdapter(fileSpec, proguardRules)
}

