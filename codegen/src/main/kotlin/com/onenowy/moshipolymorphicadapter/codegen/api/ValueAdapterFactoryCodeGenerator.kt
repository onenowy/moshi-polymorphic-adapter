package com.onenowy.moshipolymorphicadapter.codegen.api

import com.onenowy.moshipolymorphicadapter.codegen.annotations.ValueAdaterFactoryCodegen
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.ValueAdapterFactory
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.LabelValue
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.metadata.toImmutableKmClass

class ValueAdapterFactoryCodeGenerator(targetSealedClass: TargetSealedClass, private val valueAdapterFactoryCodegen: ValueAdaterFactoryCodegen) :
    AbstractAdapterFactoryCodeGenerator
        (targetSealedClass) {
    private val annotation = LabelValue::class.java
    override fun generateCode(): CodeBlock {
        val labelType = valueAdapterFactoryCodegen.labelType
        return buildCodeBlock {
            addStatement(
                "var adapterFactory = %T.of(%T::class.java, %S, %T.%L)",
                ValueAdapterFactory::class,
                targetSealedClass.baseType.toImmutableKmClass().toClassName(),
                valueAdapterFactoryCodegen.labelKey,
                labelType::class,
                labelType
            )
            for (type in targetSealedClass.subClass) {
                val labelValue = type.getAnnotation(annotation)
                addStatement("adapterFactory = adapterFactory.withSubtypeForLabelString(%T::class.java, %S)", type.toImmutableKmClass().toClassName(), labelValue.value)
            }
            addStatement("return adapterFactory")
        }
    }
}