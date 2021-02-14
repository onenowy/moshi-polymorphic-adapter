package com.onenowy.moshipolymorphicadapter.codegen.api

import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.MoshiPolymorphicAdapterFactory
import com.squareup.kotlinpoet.*
import javax.lang.model.util.Elements

fun adapterFactoryGenerator(adapterFactoryCodeGenerator: AbstractAdapterFactoryCodeGenerator, postfix: String, elements: Elements): FileSpec {
    val baseType = adapterFactoryCodeGenerator.targetSealedClass.baseType
    val typeName = "Generated${baseType.qualifiedName}$postfix"
    val funSpec = FunSpec.builder("getAdapterFactory").returns(
        MoshiPolymorphicAdapterFactory::class.asClassName().parameterizedBy(Any::class.asTypeName().copy(nullable = true), Any::class.asTypeName().copy(nullable = true))
    ).addCode(adapterFactoryCodeGenerator.generateCode()).build()
    val typeSpec = TypeSpec.classBuilder(typeName).addFunction(funSpec).addOriginatingElement(baseType).build()
    return FileSpec.builder(elements.getPackageOf(baseType).qualifiedName.toString(), typeName).addType(typeSpec).build()
}