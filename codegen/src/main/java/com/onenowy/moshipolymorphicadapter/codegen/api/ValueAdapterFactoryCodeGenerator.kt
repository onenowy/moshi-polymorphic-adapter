package com.onenowy.moshipolymorphicadapter.codegen.api

import com.onenowy.moshipolymorphicadapter.codegen.annotations.CodegenValueAdaterFactory
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.ValueAdapterFactory
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.LabelValue
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.toSupportedTypeOrNull
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.buildCodeBlock

class ValueAdapterFactoryCodeGenerator(targetSealedClass: TargetSealedClass, private val codegenValueAdapterFactory: CodegenValueAdaterFactory) :
    AbstractAdapterFactoryCodeGenerator
        (targetSealedClass) {
    private val annotation = LabelValue::class.java
    override fun generateCode(): CodeBlock {
        return buildCodeBlock {
            add(
                "var adapterFactory = %T.of(%T::Class.java, %S, %T::Class.java)",
                ValueAdapterFactory::class,
                targetSealedClass.baseType,
                codegenValueAdapterFactory.labelKey,
                codegenValueAdapterFactory.labelType
            )
            for (type in targetSealedClass.subClass) {
                val labelField = type.getAnnotation(annotation)
                add(
                    "adapterFactory = adapterFactory.withSubtype(%T::class.java, %L)",
                    type,
                    labelField.value.toSupportedTypeOrNull(codegenValueAdapterFactory.labelType.java)
                )
            }
            add("return adapterFactory")
        }
    }
}