package com.example.moshipolymorphicadapter

import com.squareup.moshi.*
import java.lang.reflect.Type


class PropertyNamePolymorphicAdapterFactory<T> @JvmOverloads constructor(
    private val baseType: Class<T>, private val subTypes: List<Type> = emptyList(), private val keyPropertyNames: List<String> = emptyList(),
    private val fallbackAdapter: JsonAdapter<Any>? = null
) : JsonAdapter.Factory {

    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (Types.getRawType(type) != baseType || annotations.isNotEmpty()) {
            return null
        }
        val jsonAdapters: List<JsonAdapter<Any>> = subTypes.map { moshi.adapter(it) }
        return PropertyNamePolymorphicAdapter(subTypes, keyPropertyNames, jsonAdapters, fallbackAdapter).nullSafe()
    }

    companion object {
        @JvmStatic
        fun <T> of(baseType: Class<T>): PropertyNamePolymorphicAdapterFactory<T> {
            return PropertyNamePolymorphicAdapterFactory(baseType)
        }
    }

    fun withSubtype(subType: Class<out T>, keyPropertyName: String): PropertyNamePolymorphicAdapterFactory<T> {
        if (keyPropertyNames.contains(keyPropertyName)) {
            throw IllegalArgumentException("Key property name must be unique")
        }
        if (subTypes.contains(subType)) {
            throw IllegalArgumentException("Duplicate subtypes are not allowed")
        }
        val newSubTypes = subTypes.toMutableList()
        newSubTypes.add(subType)
        val newKeyPropertyNames = keyPropertyNames.toMutableList()
        newKeyPropertyNames.add(keyPropertyName)
        return PropertyNamePolymorphicAdapterFactory(baseType, newSubTypes, newKeyPropertyNames, fallbackAdapter)
    }

    fun withSubTypes(subTypes: List<Type>, keyPropertyNames: List<String>): PropertyNamePolymorphicAdapterFactory<T> {
        if (keyPropertyNames.size != keyPropertyNames.distinct().size) {
            throw IllegalArgumentException("Key property name must be unique")
        }
        if (subTypes.size != subTypes.distinct().size) {
            throw IllegalArgumentException("Duplicate subtypes are not allowed")
        }
        if (keyPropertyNames.size != subTypes.size) {
            throw IllegalArgumentException("The number of Key property names is different from subtypes")
        }
        return PropertyNamePolymorphicAdapterFactory(baseType, subTypes, keyPropertyNames, fallbackAdapter)
    }

    fun withFallbackJsonAdapter(fallbackJsonAdapter: JsonAdapter<Any>): PropertyNamePolymorphicAdapterFactory<T> {
        return PropertyNamePolymorphicAdapterFactory(baseType, subTypes, keyPropertyNames, fallbackJsonAdapter)
    }


    class PropertyNamePolymorphicAdapter @JvmOverloads constructor(
        private val subTypes: List<Type>,
        private val keyPropertyNames: List<String>,
        private val jsonAdapters: List<JsonAdapter<Any>>,
        private val fallbackAdapter: JsonAdapter<Any>?,
        private val keyOptions: JsonReader.Options = JsonReader.Options.of(*keyPropertyNames.toTypedArray())
    ) : JsonAdapter<Any>() {

        override fun fromJson(reader: JsonReader): Any? {
            val peeked = reader.peekJson()
            peeked.setFailOnUnknown(false)
            @Suppress("NAME_SHADOWING")
            val keyIndex = peeked.use { peeked ->
                keyIndex(peeked)
            }
            return if (keyIndex == -1) {
                if (fallbackAdapter != null) {
                    fallbackAdapter.fromJson(reader)
                } else {
                    throw JsonDataException("No matching property names for keys")
                }
            } else {
                jsonAdapters[keyIndex]
            }
        }

        private fun keyIndex(reader: JsonReader): Int {
            reader.beginObject()
            while (reader.hasNext()) {
                val index = reader.selectName(keyOptions)
                if (index == -1) {
                    reader.skipName()
                    reader.skipValue()
                    continue
                }
                return index
            }
            return -1
        }

        override fun toJson(writer: JsonWriter, value: Any?) {
            val type = value?.javaClass
            val typeIndex = if (type != null) {
                subTypes.indexOf(type)
            } else {
                -1
            }

            val adapter = if (typeIndex == -1) {
                if (fallbackAdapter == null) {
                    throw IllegalArgumentException("Expected one of $subTypes but found $value, a ${value?.javaClass}. Register this subtype.")
                }
                fallbackAdapter
            } else {
                jsonAdapters[typeIndex]
            }

            writer.beginObject()
            val flattenToken = writer.beginFlatten()
            adapter.toJson(writer, value)
            writer.endFlatten(flattenToken)
            writer.endObject()
        }
    }
}