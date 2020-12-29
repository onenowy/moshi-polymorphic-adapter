package com.onenowy.moshipolymorphicadapter

import com.onenowy.moshipolymorphicadapter.annotations.LabelValue
import com.onenowy.moshipolymorphicadapter.annotations.UniqueName
import com.onenowy.moshipolymorphicadapter.annotations.ValueAdapterGenerate
import kotlin.reflect.KClass


class SealedClassAdapterFactory<S : MoshiPolymorphicAdapterFactory<S, T>, T : Any>(private val adapterFactory: S) :
    MoshiPolymorphicAdapterFactory<S, T> by adapterFactory {
    companion object {
        fun <S : MoshiPolymorphicAdapterFactory<S, T>, T : Any> of(baseType: KClass<T>): SealedClassAdapterFactory<S, T> {
            require(baseType.isSealed) { "The basetype must be sealed class" }
            val newAdapterFactory = when {
                baseType.annotations.contains(ValueAdapterGenerate::annotationClass) -> {
                    nameAdapterFactoryGenerator(baseType)
                }
                baseType.annotations.contains(ValueAdapterGenerate::annotationClass) -> {
                    val valueAdapterGenerate = baseType.annotations.find { it is ValueAdapterGenerate } as ValueAdapterGenerate
                    valueAdapterFactoryGenerator(baseType, valueAdapterGenerate.labelKey, valueAdapterGenerate.labelType)
                }
                else -> {
                    throw IllegalArgumentException("No adaptergenerate annotation found in ${baseType.simpleName}")
                }
            } as S
            return SealedClassAdapterFactory(newAdapterFactory)
        }

        private fun <T : Any> nameAdapterFactoryGenerator(baseType: KClass<T>): NameAdapterFactory<T> {
            val nameAdapterFactory = NameAdapterFactory.of(baseType.java)
            val subtypes = mutableListOf<Class<out T>>()
            val keyPropertyNames = mutableListOf<String>()
            baseType.sealedSubclasses.forEach { subclass ->
                for (member in subclass.members) {
                    if (member.annotations.contains(UniqueName::annotationClass)) {
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
                val labelValue = subclass.annotations.find { it is LabelValue } as? LabelValue
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
}