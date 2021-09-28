package com.onenowy.moshipolymorphicadapter.sealedclasscodegen.api

import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.ValuePolymorphicAdapterFactory
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.ValueLabel
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.ValuePolymorphicAdapter
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.metadata.toKmClass

class ValueAdapterFactoryCodeGenerator(
    targetSealedClass: TargetSealedClass,
    private val valueAdapterFactoryCodegen: ValuePolymorphicAdapter
) :
    AbstractAdapterFactoryCodeGenerator
        (targetSealedClass) {
    private val annotation = ValueLabel::class.java
    override fun generateCode(): CodeBlock {
        val labelType = valueAdapterFactoryCodegen.labelType
        return buildCodeBlock {
            addStatement(
                "var adapterFactory = %T.of(%T::class.java, %S, %T.%L, %L)",
                ValuePolymorphicAdapterFactory::class,
                targetSealedClass.baseType.toKmClass().toClassName(),
                valueAdapterFactoryCodegen.labelKey,
                labelType::class,
                labelType,
                valueAdapterFactoryCodegen.subTypeIncludeLabelKey
            )
            for (type in targetSealedClass.subClass) {
                val labelValue = type.getAnnotation(annotation)
                addStatement(
                    "adapterFactory = adapterFactory.withSubtypeForLabelString(%T::class.java, %S)",
                    type.toKmClass().toClassName(),
                    labelValue.value
                )
            }
            addStatement("return adapterFactory")
        }
    }
}