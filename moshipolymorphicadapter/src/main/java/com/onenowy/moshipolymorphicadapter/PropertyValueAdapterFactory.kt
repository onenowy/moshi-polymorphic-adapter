package com.onenowy.moshipolymorphicadapter

import com.squareup.moshi.*
import java.lang.reflect.Type

class PropertyValueAdapterFactory<T, K> @JvmOverloads constructor(
    baseType: Class<T>,
    private val labelKey: String,
    private val labelType: Class<K>,
    subTypes: List<Type> = emptyList(),
    private val labels: List<K> = emptyList(),
    fallbackAdapter: JsonAdapter<Any>? = null
) : JsonAdapter.Factory, MoshiPolyMorphicAdapterFactory<T>(baseType, subTypes, fallbackAdapter) {

    companion object {
        @JvmStatic
        fun <T, K> of(baseType: Class<T>, labelKey: String, labelType: Class<K>): PropertyValueAdapterFactory<T, K> {
            return PropertyValueAdapterFactory(baseType, labelKey, labelType)
        }
    }

    fun withSubType(subType: Class<out T>, label: K): PropertyValueAdapterFactory<T, K> {
        if (labels.contains(label)) {
            throw IllegalArgumentException("Labels must be unique")
        }
        val newSubTypes = subTypes.toMutableList()
        newSubTypes.add(subType)
        val newLabels = labels.toMutableList()
        newLabels.add(label)
        return PropertyValueAdapterFactory(baseType, labelKey, labelType, newSubTypes, newLabels)
    }

    fun withSubTypes(subTypes: List<Type>, labels: List<K>): PropertyValueAdapterFactory<T, K> {
        if (labels.size != labels.distinct().size) {
            throw IllegalArgumentException("Key property name must be unique")
        }
        if (labels.size != subTypes.size) {
            throw IllegalArgumentException("The number of Key property names is different from subtypes")
        }
        return PropertyValueAdapterFactory(baseType, labelKey, labelType, subTypes, labels, fallbackAdapter)
    }

    fun withFallbackJsonAdapter(fallbackJsonAdapter: JsonAdapter<Any>): PropertyValueAdapterFactory<T, K> {
        return PropertyValueAdapterFactory(baseType, labelKey, labelType, subTypes, labels, fallbackJsonAdapter)
    }

    fun withDefaultValue(defaultValue: T?): PropertyValueAdapterFactory<T, K> {
        return withFallbackJsonAdapter(buildFallbackJsonAdapter(defaultValue))
    }

    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (Types.getRawType(type) != baseType || annotations.isNotEmpty()) {
            return null
        }
        val jsonAdapters: List<JsonAdapter<Any>> = subTypes.map { moshi.adapter(it) }
        TODO("Not yet implemented")
    }

    class PropertyValueAdapter<K> @JvmOverloads constructor(
        private val labelKey: String,
        private val labelType: Class<K>,
        private val subTypes: List<Type>,
        private val labels: List<K>,
        private val fallbackAdapter: JsonAdapter<Any>?,
        private val keyOption: JsonReader.Options = JsonReader.Options.of(labelKey)
    ) : JsonAdapter<Any>() {
        override fun fromJson(reader: JsonReader): Any? {
            TODO("Not yet implemented")
        }

        override fun toJson(writer: JsonWriter, value: Any?) {
            TODO("Not yet implemented")
        }

    }
}