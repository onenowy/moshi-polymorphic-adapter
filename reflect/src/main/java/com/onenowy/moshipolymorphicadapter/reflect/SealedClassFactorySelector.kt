package com.onenowy.moshipolymorphicadapter.reflect

import com.onenowy.moshipolymorphicadapter.MoshiPolymorphicAdapterFactory
import com.onenowy.moshipolymorphicadapter.NameAdapterFactory
import com.onenowy.moshipolymorphicadapter.ValueAdapterFactory
import com.onenowy.moshipolymorphicadapter.annotations.LabelField
import com.onenowy.moshipolymorphicadapter.annotations.LabelValue
import com.onenowy.moshipolymorphicadapter.reflect.annotations.ReflectNameAdapterFactory
import com.onenowy.moshipolymorphicadapter.reflect.annotations.ReflectValueAdaterFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class SealedClassFactorySelector<T : Any>(private val baseType: KClass<T>) {

    init {
        require(baseType.isSealed) { "${baseType.simpleName} must be sealed class" }
    }

    fun getAdapterFactory(): MoshiPolymorphicAdapterFactory<*, T> {
        return if (baseType.findAnnotation<ReflectNameAdapterFactory>() != null) {
            nameAdapterFactoryGenerator(baseType)
        } else {
            val valueAdapterGenerate = baseType.findAnnotation<ReflectValueAdaterFactory>()
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
                labelFieldNames.add(labelField.FieldName)
                subtypes.add(subclass.java)
            }
        }
        return nameAdapterFactory.withSubTypes(subtypes, labelFieldNames)
    }

    private fun <T : Any, K : Any> valueAdapterFactoryGenerator(baseType: KClass<T>, labelKey: String, labelType: KClass<K>): ValueAdapterFactory<T, K> {
        val valueAdapterFactory = ValueAdapterFactory.of(baseType.java, labelKey, labelType.java)
        val subtypes = mutableListOf<Class<out T>>()
        val labels = mutableListOf<K>()
        baseType.sealedSubclasses.forEach { subclass ->
            val labelValue = subclass.findAnnotation<LabelValue>()
            if (labelValue != null) {
                labels.add(getValue(labelValue.value, labelType))
                subtypes.add(subclass.java)
            }
        }
        return valueAdapterFactory.withSubTypes(subtypes, labels)
    }

    private fun <K : Any> getValue(stringValue: String, labelType: KClass<K>): K {
        return when (labelType) {
            String::class -> stringValue
            Boolean::class -> stringValue.toBoolean()
            Byte::class -> stringValue.toByte()
            Short::class -> stringValue.toShort()
            Int::class -> stringValue.toInt()
            Long::class -> stringValue.toLong()
            Float::class -> stringValue.toFloat()
            Double::class -> stringValue.toDouble()
            else -> throw IllegalArgumentException("Not Supported Type ${labelType.simpleName}")
        } as K
    }
}
