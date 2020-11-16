package com.onenowy.moshipolymorphicadapter

import com.squareup.moshi.*
import java.lang.reflect.Type

class PropertyValueAdapterFactory<T, K : Any> @JvmOverloads constructor(
    baseType: Class<T>,
    private val labelKey: String,
    subTypes: List<Type> = emptyList(),
    private val labels: List<K> = emptyList(),
    fallbackAdapter: JsonAdapter<Any>? = null
) : AbstractMoshiPolymorphicAdapterFactory<T>(baseType, subTypes, fallbackAdapter) {

    companion object {
        @JvmStatic
        fun <T, K : Any> of(baseType: Class<T>, labelKey: String, labelType: Class<K>): PropertyValueAdapterFactory<T, K> {
            require((labelType.isPrimitive && labelType != Char::class.java) || Number::class.java.isAssignableFrom(labelType) || labelType == Boolean::class.javaObjectType || labelType == String::class.java)
            { "Expected Boolean, a subclass of Number or String, But found ${labelType.simpleName}" }
            return PropertyValueAdapterFactory(baseType, labelKey)
        }
    }

    fun withSubType(subType: Class<out T>, label: K): PropertyValueAdapterFactory<T, K> {
        require(!labels.contains(label)) { "Labels must be  unique" }
        val newSubTypes = subTypes.toMutableList()
        newSubTypes.add(subType)
        val newLabels = labels.toMutableList()
        newLabels.add(label)
        return PropertyValueAdapterFactory(baseType, labelKey, newSubTypes, newLabels)
    }

    fun withSubTypes(subTypes: List<Class<out T>>, labels: List<K>): PropertyValueAdapterFactory<T, K> {
        require(labels.size == labels.distinct().size) { "Key property name must be unique" }
        require(labels.size == subTypes.size) { "The number of Key property names is different from subtypes" }
        return PropertyValueAdapterFactory(baseType, labelKey, subTypes, labels, fallbackAdapter)
    }

    fun withFallbackJsonAdapter(fallbackJsonAdapter: JsonAdapter<Any>): PropertyValueAdapterFactory<T, K> {
        return PropertyValueAdapterFactory(baseType, labelKey, subTypes, labels, fallbackJsonAdapter)
    }

    fun withDefaultValue(defaultValue: T?): PropertyValueAdapterFactory<T, K> {
        return withFallbackJsonAdapter(buildFallbackJsonAdapter(defaultValue))
    }

    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (Types.getRawType(type) != baseType || annotations.isNotEmpty()) {
            return null
        }
        val jsonAdapters: List<JsonAdapter<Any>> = subTypes.map { moshi.adapter(it) }
        return PropertyValueAdapter(labelKey, subTypes, labels, fallbackAdapter, jsonAdapters)
    }

    class PropertyValueAdapter<K : Any> @JvmOverloads constructor(
        private val labelKey: String,
        private val subTypes: List<Type>,
        private val labels: List<K>,
        private val fallbackAdapter: JsonAdapter<Any>?,
        private val jsonAdapters: List<JsonAdapter<Any>>,
        private val keyOption: JsonReader.Options = JsonReader.Options.of(labelKey)
    ) : JsonAdapter<Any>() {

        override fun fromJson(reader: JsonReader): Any? {
            val peeked = reader.peekJson()
            peeked.setFailOnUnknown(false)
            val keyIndex = keyIndex(peeked)
            return if (keyIndex == -1) {
                fallbackAdapter!!.fromJson(reader)
            } else {
                jsonAdapters[keyIndex].fromJson(reader)
            }

        }

        private fun keyIndex(reader: JsonReader): Int {
            reader.beginObject()
            while (reader.hasNext()) {
                if (reader.selectName(keyOption) == -1) {
                    reader.skipName()
                    reader.skipValue()
                    continue
                }
                val token = reader.peek()
                val labelValue = when (val labelType = labels.lastOrNull()) {
                    is Boolean -> if (token == JsonReader.Token.BOOLEAN) reader.nextBoolean() else null
                    is String -> if (token == JsonReader.Token.STRING) reader.nextString() else null
                    is Number -> if (token == JsonReader.Token.NUMBER) getNumber(reader, labelType) else null
                    else -> null
                }
                val index = labels.indexOf(labelValue)
                if (index == -1) {
                    if (fallbackAdapter == null) {
                        throw JsonDataException("Expected one of $labels for key '$labelKey' but found '${labelValue}'. Register a subtype for this label.")
                    }
                }
                return index
            }
            throw JsonDataException("Missing label for $labelKey")
        }

        private fun getNumber(reader: JsonReader, labelType: K?): Number? {
            val stringNumber = reader.nextString()

            return when (labelType) {
                is Byte -> stringNumber.toByteOrNull()
                is Short -> stringNumber.toShortOrNull()
                is Int -> stringNumber.toIntOrNull()
                is Long -> stringNumber.toLongOrNull()
                is Float -> stringNumber.toFloatOrNull()
                is Double -> stringNumber.toDoubleOrNull()
                else -> null
            }
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
            if (adapter != fallbackAdapter) {
                writeValue(writer.name(labelKey), labels[typeIndex])
            }
            val flattenToken = writer.beginFlatten()
            adapter.toJson(writer, value)
            writer.endFlatten(flattenToken)
            writer.endObject()
        }

        private fun writeValue(writer: JsonWriter, value: Any): JsonWriter {
            return when (value) {
                is Number -> writer.value(value)
                is Boolean -> writer.value(value)
                is String -> writer.value(value)
                else -> throw IllegalArgumentException("${value.javaClass.canonicalName} is not supported")
            }
        }
    }
}
