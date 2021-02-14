package com.onenowy.moshipolymorphicadapter.codegen.api

import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.NameAdapterFactory
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.LabelField
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.buildCodeBlock

class NameAdapterFactoryCodeGenerator(targetSealedClass: TargetSealedClass) : AbstractAdapterFactoryCodeGenerator(targetSealedClass) {
    private val annotation = LabelField::class.java
    override fun generateCode(): CodeBlock {
        return buildCodeBlock {
            add("var adapterFactory = %T.of(%T::Class.java)", NameAdapterFactory::class, targetSealedClass.baseType)
            for (type in targetSealedClass.subClass) {
                val labelField = type.getAnnotation(annotation)
                add("adapterFactory = adapterFactory.withSubtype(%T::class.java, %S)", type, labelField.fieldName)
            }
            add("return adapterFactory")
        }
    }
}