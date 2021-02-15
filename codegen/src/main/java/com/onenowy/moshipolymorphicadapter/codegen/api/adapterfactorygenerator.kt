package com.onenowy.moshipolymorphicadapter.codegen.api

import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.MoshiPolymorphicAdapterFactory
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.lang.model.util.Elements

fun adapterFactoryGenerator(adapterFactoryCodeGenerator: AbstractAdapterFactoryCodeGenerator, postfix: String, elements: Elements): FileSpec {
    val baseType = adapterFactoryCodeGenerator.targetSealedClass.baseType
    val typeName = "Generated${baseType.simpleName}$postfix"
    val starProjection = WildcardTypeName.producerOf(Any::class.asTypeName().copy(nullable = true))
    val funSpec = FunSpec.builder("invoke").addModifiers(KModifier.OPERATOR).returns(
        MoshiPolymorphicAdapterFactory::class.asClassName().parameterizedBy
            (starProjection, baseType.asClassName())
    )
        .addCode(adapterFactoryCodeGenerator.generateCode()).build()
    val typeSpec = TypeSpec.classBuilder(typeName).addFunction(funSpec).addOriginatingElement(baseType).build()
    return FileSpec.builder(elements.getPackageOf(baseType).qualifiedName.toString(), typeName).addType(typeSpec).build()
}