package com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory

import com.squareup.moshi.*
import java.lang.reflect.Type

class NameAdapterFactory<T> @JvmOverloads constructor(
    private val baseType: Class<T>,
    private val subTypes: List<Type> = emptyList(),
    private val labelFieldNames: List<String> = emptyList(),
    private val fallbackAdapter: JsonAdapter<Any>? = null
) : MoshiPolymorphicAdapterFactory<NameAdapterFactory<T>, T> {

    override fun create(
        type: Type,
        annotations: MutableSet<out Annotation>,
        moshi: Moshi
    ): JsonAdapter<*>? {
        if (Types.getRawType(type) != baseType || annotations.isNotEmpty()) {
            return null
        }

        val jsonAdapters: List<JsonAdapter<Any>> = subTypes.map { moshi.adapter(it) }
        return NameAdapter(subTypes, labelFieldNames, jsonAdapters, fallbackAdapter).nullSafe()
    }

    companion object {
        @JvmStatic
        fun <T> of(baseType: Class<T>): NameAdapterFactory<T> {
            return NameAdapterFactory(baseType)
        }
    }


    fun withSubtype(subType: Class<out T>, labelName: String): NameAdapterFactory<T> {
        require(!labelFieldNames.contains(labelName)) { "$labelName must be unique" }
        val newSubTypes = subTypes.toMutableList()
        newSubTypes.add(subType)
        val newLabelFieldNames = labelFieldNames.toMutableList()
        newLabelFieldNames.add(labelName)
        return NameAdapterFactory(baseType, newSubTypes, newLabelFieldNames, fallbackAdapter)
    }

    fun withSubtypes(
        subTypes: List<Class<out T>>,
        labelNames: List<String>
    ): NameAdapterFactory<T> {
        require(labelNames.size == labelNames.distinct().size) { "Label Field name for ${baseType.simpleName} must be unique" }
        require(labelNames.size == subTypes.size) { "The number of Label Field names for ${baseType.simpleName} is different from subtypes" }

        return NameAdapterFactory(baseType, subTypes, labelNames, fallbackAdapter)
    }

    override fun withFallbackJsonAdapter(fallbackJsonAdapter: JsonAdapter<Any>): NameAdapterFactory<T> {
        return NameAdapterFactory(baseType, subTypes, labelFieldNames, fallbackJsonAdapter)
    }

    override fun withDefaultValue(defaultValue: T?): NameAdapterFactory<T> {
        return withFallbackJsonAdapter(buildFallbackJsonAdapter(defaultValue))
    }

    class NameAdapter @JvmOverloads constructor(
        private val subTypes: List<Type>,
        private val labelNames: List<String>,
        private val jsonAdapters: List<JsonAdapter<Any>>,
        private val fallbackAdapter: JsonAdapter<Any>?,
        private val NameOptions: JsonReader.Options = JsonReader.Options.of(*labelNames.toTypedArray())
    ) : JsonAdapter<Any>() {

        override fun fromJson(reader: JsonReader): Any? {
            val peeked = reader.peekJson()
            peeked.setFailOnUnknown(false)
            val labelIndex = labelIndex(peeked)
            return if (labelIndex == -1) {
                if (fallbackAdapter != null) {
                    fallbackAdapter.fromJson(reader)
                } else {
                    throw JsonDataException("No matching Field names for $labelNames")
                }
            } else {
                jsonAdapters[labelIndex].fromJson(reader)
            }
        }

        private fun labelIndex(reader: JsonReader): Int {
            reader.beginObject()
            while (reader.hasNext()) {
                val index = reader.selectName(NameOptions)
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
                require(fallbackAdapter != null) { "Expected one of $subTypes but found $value, a ${value?.javaClass}. Register this subtype." }
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