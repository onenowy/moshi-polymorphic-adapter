package com.onenowy.moshipolymorphicadapter.reflect

import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.*
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.LabelName
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.LabelValue
import com.onenowy.moshipolymorphicadapter.reflect.annotations.NameAdapterFactoryReflection
import com.onenowy.moshipolymorphicadapter.reflect.annotations.ValueAdaterFactoryReflection
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class SealedClassFactorySelector<T : Any>(private val baseType: KClass<T>) {

    init {
        require(baseType.isSealed) { "${baseType.simpleName} must be sealed class" }
    }

    fun getAdapterFactory(): MoshiPolymorphicAdapterFactory<*, T> {
        return if (baseType.findAnnotation<NameAdapterFactoryReflection>() != null) {
            nameAdapterFactoryGenerator(baseType)
        } else {
            val valueAdapterGenerate = baseType.findAnnotation<ValueAdaterFactoryReflection>()
            if (valueAdapterGenerate != null) {
                valueAdapterFactoryGenerator(baseType, valueAdapterGenerate.labelKey, valueAdapterGenerate.labelType)
            } else {
                throw IllegalArgumentException("No Adapter Factory Annotations found in ${baseType.simpleName}")
            }
        }
    }


    private fun <T : Any> nameAdapterFactoryGenerator(baseType: KClass<T>): NameAdapterFactory<T> {
        val nameAdapterFactory = NameAdapterFactory.of(baseType.java)
        val subtypes = mutableListOf<Class<out T>>()
        val labelNames = mutableListOf<String>()
        baseType.sealedSubclasses.forEach { subclass ->
            val labelField = subclass.findAnnotation<LabelName>()
            if (labelField != null) {
                labelNames.add(labelField.name)
                subtypes.add(subclass.java)
            }
        }
        return nameAdapterFactory.withSubtypes(subtypes, labelNames)
    }

    private fun <T : Any> valueAdapterFactoryGenerator(baseType: KClass<T>, labelKey: String, labelType: SupportValueType): ValueAdapterFactory<T> {
        val valueAdapterFactory = ValueAdapterFactory.of(baseType.java, labelKey, labelType)
        val subtypes = mutableListOf<Class<out T>>()
        val labelValues = mutableListOf<Any>()
        baseType.sealedSubclasses.forEach { subclass ->
            val labelValue = subclass.findAnnotation<LabelValue>()
            if (labelValue != null) {
                val value = labelValue.value.toSupportedTypeOrNull(labelType) ?: throw IllegalArgumentException("Not Supported Type ${labelType.name}")
                labelValues.add(value)
                subtypes.add(subclass.java)
            }
        }
        return valueAdapterFactory.withSubtypes(subtypes, labelValues)
    }
}
