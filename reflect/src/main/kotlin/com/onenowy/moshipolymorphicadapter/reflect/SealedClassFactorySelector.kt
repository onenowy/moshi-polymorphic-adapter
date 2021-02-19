package com.onenowy.moshipolymorphicadapter.reflect

import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.MoshiPolymorphicAdapterFactory
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.NameAdapterFactory
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.ValueAdapterFactory
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.LabelField
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.LabelValue
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.NameAdapterFactoryReflection
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.ValueAdaterFactoryReflection
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.toSupportedTypeOrNull
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
            SealedClassFactorySelector::class
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
        val labelFieldNames = mutableListOf<String>()
        baseType.sealedSubclasses.forEach { subclass ->
            val labelField = subclass.findAnnotation<LabelField>()
            if (labelField != null) {
                labelFieldNames.add(labelField.fieldName)
                subtypes.add(subclass.java)
            }
        }
        return nameAdapterFactory.withSubtypes(subtypes, labelFieldNames)
    }

    private fun <T : Any, K : Any> valueAdapterFactoryGenerator(baseType: KClass<T>, labelKey: String, labelType: KClass<K>): ValueAdapterFactory<T, K> {
        val valueAdapterFactory = ValueAdapterFactory.of(baseType.java, labelKey, labelType.java)
        val subtypes = mutableListOf<Class<out T>>()
        val labels = mutableListOf<K>()
        baseType.sealedSubclasses.forEach { subclass ->
            val labelValue = subclass.findAnnotation<LabelValue>()
            if (labelValue != null) {
                val value = labelValue.value.toSupportedTypeOrNull(labelType.java) ?: throw IllegalArgumentException("Not Supported Type ${labelType.simpleName}")
                labels.add(value)
                subtypes.add(subclass.java)
            }
        }
        return valueAdapterFactory.withSubtypes(subtypes, labels)
    }
}
