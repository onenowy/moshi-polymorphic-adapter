package com.onenowy.moshipolymorphicadapter.codegen.api

import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.ValueAdapterFactory
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.LabelValue
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.ValueAdaterFactoryCodegen
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.buildCodeBlock
import javax.lang.model.type.MirroredTypeException

class ValueAdapterFactoryCodeGenerator(targetSealedClass: TargetSealedClass, private val valueAdapterFactoryCodegen: ValueAdaterFactoryCodegen) :
    AbstractAdapterFactoryCodeGenerator
        (targetSealedClass) {
    private val annotation = LabelValue::class.java
    override fun generateCode(): CodeBlock {
        val labelType = try {
            valueAdapterFactoryCodegen.labelType
        } catch (e: MirroredTypeException) {
            e.typeMirror
        }
        return buildCodeBlock {
            addStatement(
                "var adapterFactory = %T.of(%T::class.java, %S, %T::class.java)",
                ValueAdapterFactory::class,
                targetSealedClass.baseType,
                valueAdapterFactoryCodegen.labelKey,
                labelType
            )
            for (type in targetSealedClass.subClass) {
                val labelValue = type.getAnnotation(annotation)
                addStatement("adapterFactory = adapterFactory.withSubtype(%T::class.java, %S)", type, labelValue.value)
            }
            addStatement("return adapterFactory")
        }
    }
}