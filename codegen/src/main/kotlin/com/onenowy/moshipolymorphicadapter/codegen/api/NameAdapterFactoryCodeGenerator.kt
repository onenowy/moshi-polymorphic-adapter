package com.onenowy.moshipolymorphicadapter.codegen.api

import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.NameAdapterFactory
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.LabelName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.metadata.toKmClass


class NameAdapterFactoryCodeGenerator(targetSealedClass: TargetSealedClass) :
    AbstractAdapterFactoryCodeGenerator(targetSealedClass) {
    private val annotation = LabelName::class.java
    override fun generateCode(): CodeBlock {
        return buildCodeBlock {
            addStatement(
                "var adapterFactory = %T.of(%T::class.java)",
                NameAdapterFactory::class,
                targetSealedClass.baseType.toKmClass().toClassName()
            )
            for (type in targetSealedClass.subClass) {
                val labelField = type.getAnnotation(annotation)
                addStatement(
                    "adapterFactory = adapterFactory.withSubtype(%T::class.java, %S)",
                    type.toKmClass().toClassName(),
                    labelField.name
                )
            }
            addStatement("return adapterFactory")
        }
    }
}