package com.onenowy.moshipolymorphicadapter.sealedclasscodegen.api

import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.NamePolymorphicAdapterFactory
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.NameLabel
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.metadata.toKmClass


class NameAdapterFactoryCodeGenerator(targetSealedClass: TargetSealedClass) :
    AbstractAdapterFactoryCodeGenerator(targetSealedClass) {
    private val annotation = NameLabel::class.java
    override fun generateCode(): CodeBlock {
        return buildCodeBlock {
            addStatement(
                "var adapterFactory = %T.of(%T::class.java)",
                NamePolymorphicAdapterFactory::class,
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