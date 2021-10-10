package com.onenowy.moshipolymorphicadapter

import com.squareup.moshi.*
import java.lang.reflect.Type

class ValuePolymorphicAdapterFactory<T, V : Any> @JvmOverloads constructor(
    private val baseType: Class<T>,
    private val labelType: Class<V>,
    private val labelKey: String,
    private val subTypes: List<Type> = emptyList(),
    private val labels: List<V> = emptyList(),
    private val fallbackAdapter: JsonAdapter<Any>? = null,
) : AbstractMoshiPolymorphicAdapterFactory<ValuePolymorphicAdapterFactory<T, V>, T>() {

    companion object {

        @JvmStatic
        fun <T, V : Any> of(
            baseType: Class<T>,
            labelKey: String,
            labelType: Class<V>
        ): ValuePolymorphicAdapterFactory<T, V> {
            return ValuePolymorphicAdapterFactory(baseType, labelType, labelKey)
        }
    }

    fun withSubtype(subType: Class<out T>, valueLabel: V): ValuePolymorphicAdapterFactory<T, V> {
        require(!labels.contains(valueLabel)) { "$valueLabel must be unique" }
        val newSubTypes = subTypes.toMutableList()
        newSubTypes.add(subType)
        val newLabels = labels.toMutableList()
        newLabels.add(valueLabel)
        return ValuePolymorphicAdapterFactory(
            baseType,
            labelType,
            labelKey,
            newSubTypes,
            newLabels
        )
    }

    fun withSubtypes(subTypes: List<Class<out T>>, valueLabels: List<V>): ValuePolymorphicAdapterFactory<T, V> {
        require(valueLabels.size == valueLabels.distinct().size) { "The value for ${baseType.simpleName} must be unique" }
        require(valueLabels.size == subTypes.size) { "The number of values for ${baseType.simpleName} is different from subtypes" }
        val newSubTypes = this.subTypes.toMutableList()
        newSubTypes.addAll(subTypes)
        val newLabels = labels.toMutableList()
        newLabels.addAll(valueLabels)
        return ValuePolymorphicAdapterFactory(
            baseType,
            labelType,
            labelKey,
            newSubTypes,
            newLabels,
            fallbackAdapter
        )
    }

    override fun withFallbackJsonAdapter(fallbackJsonAdapter: JsonAdapter<Any>): ValuePolymorphicAdapterFactory<T, V> {
        return ValuePolymorphicAdapterFactory(
            baseType,
            labelType,
            labelKey,
            subTypes,
            labels,
            fallbackJsonAdapter
        )
    }

    override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (Types.getRawType(type) != baseType || annotations.isNotEmpty()) {
            return null
        }
        val jsonAdapters: List<JsonAdapter<Any>> = subTypes.map { moshi.adapter(it) }
        return ValueAdapter(
            labelKey,
            labelType,
            subTypes,
            labels,
            fallbackAdapter,
            jsonAdapters
        )
    }

    class ValueAdapter<V> @JvmOverloads constructor(
        private val labelKey: String,
        private val labelType: Class<V>,
        private val subTypes: List<Type>,
        private val labels: List<V>,
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
            @Suppress("UNCHECKED_CAST")
            while (reader.hasNext()) {
                if (reader.selectName(keyOption) == -1) {
                    reader.skipName()
                    reader.skipValue()
                    continue
                }
                val labelValue = when (getValueAdaterTypeOrNull(labelType)) {
                    AdapterType.VALUE_ADAPTER.STRING -> reader.nextString()
                    AdapterType.VALUE_ADAPTER.BOOLEAN -> reader.nextBoolean()
                    AdapterType.VALUE_ADAPTER.INT -> reader.nextInt()
                    AdapterType.VALUE_ADAPTER.LONG -> reader.nextLong()
                    AdapterType.VALUE_ADAPTER.DOUBLE -> reader.nextDouble()
                    else -> null
                }
                val index = if (labelValue != null) labels.indexOf(labelValue as V) else -1
                if (index == -1) {
                    if (fallbackAdapter == null) {
                        throw JsonDataException("Expected one of $labels for key '$labelKey' but found '${labelValue}'. Register a subtype for this label.")
                    }
                }
                return index
            }
            if (fallbackAdapter == null) {
                throw JsonDataException("Missing label for $labelKey")
            } else {
                return -1
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
                require(fallbackAdapter != null) { "Expected one of $subTypes but found $value, a ${type}. Register this subtype." }
                fallbackAdapter
            } else {
                jsonAdapters[typeIndex]
            }
            writer.beginObject()
            if (adapter != fallbackAdapter) {
                writer.name(labelKey).jsonValue(labels[typeIndex])
            }
            val flattenToken = writer.beginFlatten()
            adapter.toJson(writer, value)
            writer.endFlatten(flattenToken)
            writer.endObject()
        }
    }
}

