package com.onenowy.moshipolymorphicadapter

import com.squareup.moshi.*
import java.lang.reflect.Type

class NameAdapterFactory<T> @JvmOverloads constructor(
    private val baseType: Class<T>, private val subTypes: List<Type> = emptyList(), private val labelFieldNames: List<String> = emptyList(),
    private val fallbackAdapter: JsonAdapter<Any>? = null
) : MoshiPolymorphicAdapterFactory<NameAdapterFactory<T>, T> {

    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (Types.getRawType(type) != baseType || annotations.isNotEmpty()) {
            return null
        }

        val jsonAdapters: List<JsonAdapter<Any>> = subTypes.map { moshi.adapter(it) }
        return FieldNameAdapter(subTypes, labelFieldNames, jsonAdapters, fallbackAdapter).nullSafe()
    }

    companion object {
        @JvmStatic
        fun <T> of(baseType: Class<T>): NameAdapterFactory<T> {
            return NameAdapterFactory(baseType)
        }
    }


    fun withSubtype(subType: Class<out T>, labelFieldName: String): NameAdapterFactory<T> {
        require(!labelFieldNames.contains(labelFieldName)) { "$labelFieldName must be unique" }
        val newSubTypes = subTypes.toMutableList()
        newSubTypes.add(subType)
        val newLabelFieldNames = labelFieldNames.toMutableList()
        newLabelFieldNames.add(labelFieldName)
        return NameAdapterFactory(baseType, newSubTypes, newLabelFieldNames, fallbackAdapter)
    }

    fun withSubTypes(subTypes: List<Class<out T>>, labelFieldNames: List<String>): NameAdapterFactory<T> {
        require(labelFieldNames.size == labelFieldNames.distinct().size) { "Label Field name for ${baseType.simpleName} must be unique" }
        require(labelFieldNames.size == subTypes.size) { "The number of Label Field names for ${baseType.simpleName} is different from subtypes" }

        return NameAdapterFactory(baseType, subTypes, labelFieldNames, fallbackAdapter)
    }

    override fun withFallbackJsonAdapter(fallbackJsonAdapter: JsonAdapter<Any>): NameAdapterFactory<T> {
        return NameAdapterFactory(baseType, subTypes, labelFieldNames, fallbackJsonAdapter)
    }

    override fun withDefaultValue(defaultValue: T?): NameAdapterFactory<T> {
        return withFallbackJsonAdapter(buildFallbackJsonAdapter(defaultValue))
    }

    class FieldNameAdapter @JvmOverloads constructor(
        private val subTypes: List<Type>,
        private val labelFieldNames: List<String>,
        private val jsonAdapters: List<JsonAdapter<Any>>,
        private val fallbackAdapter: JsonAdapter<Any>?,
        private val FieldNameOptions: JsonReader.Options = JsonReader.Options.of(*labelFieldNames.toTypedArray())
    ) : JsonAdapter<Any>() {

        override fun fromJson(reader: JsonReader): Any? {
            val peeked = reader.peekJson()
            peeked.setFailOnUnknown(false)
            val labelIndex = labelIndex(peeked)
            return if (labelIndex == -1) {
                if (fallbackAdapter != null) {
                    fallbackAdapter.fromJson(reader)
                } else {
                    throw JsonDataException("No matching Field names for $labelFieldNames")
                }
            } else {
                jsonAdapters[labelIndex].fromJson(reader)
            }
        }

        private fun labelIndex(reader: JsonReader): Int {
            reader.beginObject()
            while (reader.hasNext()) {
                val index = reader.selectName(FieldNameOptions)
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