package com.onenowy.sealedclassreflect

import com.onenowy.moshipolymorphicadapter.MoshiPolymorphicAdapterFactory
import com.onenowy.moshipolymorphicadapter.NameAdapterFactory
import com.onenowy.moshipolymorphicadapter.ValueAdapterFactory
import com.onenowy.sealedclassreflect.annotations.LabelValue
import com.onenowy.sealedclassreflect.annotations.NameFactory
import com.onenowy.sealedclassreflect.annotations.UniqueName
import com.onenowy.sealedclassreflect.annotations.ValueFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class SealedClassFactorySelector<T : Any>(private val baseType: KClass<T>) {

    init {
        require(baseType.isSealed) { "${baseType.simpleName} must be sealed class" }
    }

    fun getAdapterFactory(): MoshiPolymorphicAdapterFactory<*, T> {
        return if (baseType.findAnnotation<NameFactory>() != null) {
            nameAdapterFactoryGenerator(baseType)
        } else {
            val valueAdapterGenerate = baseType.findAnnotation<ValueFactory>()
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
        val keyPropertyNames = mutableListOf<String>()
        baseType.sealedSubclasses.forEach { subclass ->
            for (member in subclass.members) {
                if (member.findAnnotation<UniqueName>() != null) {
                    subtypes.add(subclass.java)
                    keyPropertyNames.add(member.name)
                    break
                }
            }
        }
        return nameAdapterFactory.withSubTypes(subtypes, keyPropertyNames)
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
