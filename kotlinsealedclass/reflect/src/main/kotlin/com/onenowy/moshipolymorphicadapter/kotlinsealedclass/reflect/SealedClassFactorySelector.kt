package com.onenowy.moshipolymorphicadapter.kotlinsealedclass.reflect

import com.onenowy.moshipolymorphicadapter.*
import com.onenowy.moshipolymorphicadapter.annotations.NameLabel
import com.onenowy.moshipolymorphicadapter.annotations.NamePolymorphicAdapter
import com.onenowy.moshipolymorphicadapter.annotations.ValueLabel
import com.onenowy.moshipolymorphicadapter.annotations.ValuePolymorphicAdapter
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class SealedClassFactorySelector<T : Any>(private val baseType: KClass<T>) {

    init {
        require(baseType.isSealed) { "${baseType.simpleName} must be sealed class" }
    }

    fun getAdapterFactory(): MoshiPolymorphicAdapterFactory<*, T> {
        return if (baseType.findAnnotation<NamePolymorphicAdapter>() != null) {
            nameAdapterFactoryGenerator(baseType)
        } else {
            val valueAdapterGenerate = baseType.findAnnotation<ValuePolymorphicAdapter>()
            if (valueAdapterGenerate != null) {
                valueAdapterFactoryGenerator(
                    baseType,
                    valueAdapterGenerate.labelKey,
                    valueAdapterGenerate.labelType,
                    valueAdapterGenerate.subTypeIncludeLabelKey
                )
            } else {
                throw IllegalArgumentException("No Adapter Factory Annotations found in ${baseType.simpleName}")
            }
        }
    }


    private fun <T : Any> nameAdapterFactoryGenerator(baseType: KClass<T>): NamePolymorphicAdapterFactory<T> {
        val nameAdapterFactory = NamePolymorphicAdapterFactory.of(baseType.java)
        val subtypes = mutableListOf<Class<out T>>()
        val labelNames = mutableListOf<String>()
        baseType.sealedSubclasses.forEach { subclass ->
            val labelField = subclass.findAnnotation<NameLabel>()
            if (labelField != null) {
                labelNames.add(labelField.name)
                subtypes.add(subclass.java)
            }
        }
        return nameAdapterFactory.withSubtypes(subtypes, labelNames)
    }

    private fun <T : Any> valueAdapterFactoryGenerator(
        baseType: KClass<T>,
        labelKey: String,
        labelType: SupportValueType,
        subTypeIncludeLabelKey: Boolean
    ): ValuePolymorphicAdapterFactory<T> {
        val valueAdapterFactory =
            ValuePolymorphicAdapterFactory.of(baseType.java, labelKey, labelType, subTypeIncludeLabelKey)
        val subtypes = mutableListOf<Class<out T>>()
        val labelValues = mutableListOf<Any>()
        baseType.sealedSubclasses.forEach { subclass ->
            val labelValue = subclass.findAnnotation<ValueLabel>()
            if (labelValue != null) {
                val value = labelValue.value.toSupportedTypeOrNull(labelType)
                    ?: throw IllegalArgumentException("Not Supported Type ${labelType.name}")
                labelValues.add(value)
                subtypes.add(subclass.java)
            }
        }
        return valueAdapterFactory.withSubtypes(subtypes, labelValues)
    }
}
