package com.onenowy.moshipolymorphicadapter.kotlinsealed.reflect

import com.onenowy.moshipolymorphicadapter.*
import com.onenowy.moshipolymorphicadapter.annotations.DefaultNull
import com.onenowy.moshipolymorphicadapter.annotations.NameLabel
import com.onenowy.moshipolymorphicadapter.annotations.ValueLabel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class KotlinSealedPolymorphicAdapterFactory : JsonAdapter.Factory {
    private fun <T : Any> nameAdapterFactoryGenerator(baseType: KClass<T>): NamePolymorphicAdapterFactory<T> {
        var nameAdapterFactory = NamePolymorphicAdapterFactory.of(baseType.java)
        val subtypes = mutableListOf<Class<out T>>()
        val nameLabels = mutableListOf<String>()
        baseType.sealedSubclasses.forEach { subclass ->
            val nameLabel = subclass.findAnnotation<NameLabel>()
            if (nameLabel != null) {
                nameLabels.add(nameLabel.name)
                subtypes.add(subclass.java)
            }
        }
        nameAdapterFactory = nameAdapterFactory.withSubtypes(subtypes, nameLabels)
        if (baseType.findAnnotation<DefaultNull>() != null) {
            nameAdapterFactory = nameAdapterFactory.withDefaultValue(null)
        }
        return nameAdapterFactory
    }

    private fun <T : Any, V : Any> valueAdapterFactoryGenerator(
        baseType: KClass<T>,
        labelKey: String,
        labelType: String,
        labelTypeClass: KClass<V>
    ): ValuePolymorphicAdapterFactory<T, V> {
        var valueAdapterFactory =
            ValuePolymorphicAdapterFactory.of(baseType.java, labelKey, labelTypeClass.java)
        val subtypes = mutableListOf<Class<out T>>()
        val labelValues = mutableListOf<V>()
        baseType.sealedSubclasses.forEach { subclass ->
            val labelValue = subclass.findAnnotation<ValueLabel>()
            if (labelValue != null) {
                val value = labelValue.value.toSupportTypeOrNull(labelType)
                    ?: throw IllegalArgumentException("Not Supported Type ${labelTypeClass.simpleName}")
                @Suppress("UNCHECKED_CAST")
                labelValues.add(value as V)
                subtypes.add(subclass.java)
            }
        }
        valueAdapterFactory = valueAdapterFactory.withSubtypes(subtypes, labelValues)
        if (baseType.findAnnotation<DefaultNull>() != null) {
            valueAdapterFactory = valueAdapterFactory.withDefaultValue(null)
        }
        return valueAdapterFactory
    }

    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        val baseClass = Types.getRawType(type).kotlin
        val jsonClass = baseClass.findAnnotation<JsonClass>()
        if (annotations.isNotEmpty() || jsonClass == null || !jsonClass.generator.startsWith("MoshiPolymorphic")) {
            return null
        }
        require(baseClass.isSealed) { "${baseClass.simpleName} is not a sealed class" }
        val adapterFactory = if (jsonClass.generator == PolymorphicAdapterType.NAME_POLYMORPHIC_ADAPTER) {
            nameAdapterFactoryGenerator(baseClass)
        } else {
            val generatorTag = jsonClass.generator.split(":")
            valueAdapterFactoryGenerator(
                baseClass,
                generatorTag[1],
                generatorTag[0],
                labelTypeClass = getSupportTypeClass(generatorTag[0])
            )
        }
        return adapterFactory.create(type, annotations, moshi)
    }
}
