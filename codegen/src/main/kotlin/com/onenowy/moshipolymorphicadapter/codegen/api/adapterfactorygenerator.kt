package com.onenowy.moshipolymorphicadapter.codegen.api

import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.MoshiPolymorphicAdapterFactory
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import javax.lang.model.util.Elements

fun adapterFactoryGenerator(adapterFactoryCodeGenerator: AbstractAdapterFactoryCodeGenerator, postfix: String, elements: Elements): FileSpec {
    val baseType = adapterFactoryCodeGenerator.targetSealedClass.baseType
    val typeName = "${baseType.simpleName}$postfix"
    val starProjection = WildcardTypeName.producerOf(Any::class.asTypeName().copy(nullable = true))
    val funSpec =
        FunSpec.builder("generate$typeName").returns(
            MoshiPolymorphicAdapterFactory::class.asClassName().parameterizedBy(starProjection, baseType.toImmutableKmClass().toClassName())
        ).addCode(adapterFactoryCodeGenerator.generateCode()).addOriginatingElement(baseType).build()
    return FileSpec.builder(elements.getPackageOf(baseType).qualifiedName.toString(), "${typeName}Generator").addFunction(funSpec).build()
}