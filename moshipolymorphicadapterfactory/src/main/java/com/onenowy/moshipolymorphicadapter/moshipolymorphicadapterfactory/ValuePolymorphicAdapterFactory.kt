package com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory

import com.squareup.moshi.*
import java.lang.reflect.Type

class ValuePolymorphicAdapterFactory<T> @JvmOverloads constructor(
    private val subTypeIncludeLabelKey: Boolean,
    private val baseType: Class<T>,
    private val labelType: SupportValueType,
    private val labelKey: String,
    private val subTypes: List<Type> = emptyList(),
    private val labels: List<Any> = emptyList(),
    private val fallbackAdapter: JsonAdapter<Any>? = null,
) : MoshiPolymorphicAdapterFactory<ValuePolymorphicAdapterFactory<T>, T> {

    companion object {
        @JvmStatic
        @JvmOverloads
        fun <T> of(
            baseType: Class<T>,
            labelKey: String,
            labelType: SupportValueType,
            subTypeIncludeLabelKey: Boolean = false
        ): ValuePolymorphicAdapterFactory<T> {
            return ValuePolymorphicAdapterFactory(subTypeIncludeLabelKey, baseType, labelType, labelKey)
        }
    }

    fun withSubtype(subType: Class<out T>, label: Any): ValuePolymorphicAdapterFactory<T> {
        require(label.typeCheck(labelType)) { "the type of $label is not ${labelType.name}" }
        require(!labels.contains(label)) { "$label must be unique" }
        val newSubTypes = subTypes.toMutableList()
        newSubTypes.add(subType)
        val newLabels = labels.toMutableList()
        newLabels.add(label)
        return ValuePolymorphicAdapterFactory(
            subTypeIncludeLabelKey,
            baseType,
            labelType,
            labelKey,
            newSubTypes,
            newLabels
        )
    }

    fun withSubtypeForLabelString(subType: Class<out T>, label: String): ValuePolymorphicAdapterFactory<T> {
        return withSubtype(
            subType,
            label.toSupportedTypeOrNull(labelType) ?: throw IllegalArgumentException("$label is not supported type")
        )
    }

    fun withSubtypes(subTypes: List<Class<out T>>, valueLabels: List<Any>): ValuePolymorphicAdapterFactory<T> {
        require(valueLabels.size == valueLabels.distinct().size) { "Key property name for ${baseType.simpleName} must be unique" }
        require(valueLabels.size == subTypes.size) { "The number of Key property names for ${baseType.simpleName} is different from subtypes" }
        return ValuePolymorphicAdapterFactory(
            subTypeIncludeLabelKey,
            baseType,
            labelType,
            labelKey,
            subTypes,
            valueLabels,
            fallbackAdapter
        )
    }

    override fun withFallbackJsonAdapter(fallbackJsonAdapter: JsonAdapter<Any>): ValuePolymorphicAdapterFactory<T> {
        return ValuePolymorphicAdapterFactory(
            subTypeIncludeLabelKey,
            baseType,
            labelType,
            labelKey,
            subTypes,
            labels,
            fallbackJsonAdapter
        )
    }

    override fun withDefaultValue(defaultValue: T?): ValuePolymorphicAdapterFactory<T> {
        return withFallbackJsonAdapter(buildFallbackJsonAdapter(defaultValue))
    }

    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (Types.getRawType(type) != baseType || annotations.isNotEmpty()) {
            return null
        }
        val jsonAdapters: List<JsonAdapter<Any>> = subTypes.map { moshi.adapter(it) }
        return ValueAdapter(
            subTypeIncludeLabelKey,
            labelKey,
            labelType,
            subTypes,
            labels,
            fallbackAdapter,
            jsonAdapters
        )
    }

    class ValueAdapter @JvmOverloads constructor(
        private val subTypeIncludeLabelKey: Boolean,
        private val labelKey: String,
        private val labelType: SupportValueType,
        private val subTypes: List<Type>,
        private val labels: List<Any>,
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
                val labelValue = if (labelType == SupportValueType.BOOLEAN && token == JsonReader.Token.BOOLEAN) reader.nextBoolean()
                else if (labelType == SupportValueType.STRING && token == JsonReader.Token.STRING) reader.nextString()
                else if (labelType in arrayOf(SupportValueType.BYTE, SupportValueType.SHORT, SupportValueType.INT) && token == JsonReader.Token.NUMBER) reader.nextInt()
                else if (labelType == SupportValueType.LONG && token == JsonReader.Token.NUMBER) reader.nextLong()
                else if (labelType in arrayOf(SupportValueType.DOUBLE, SupportValueType.FLOAT) && token == JsonReader.Token.NUMBER) reader.nextDouble()
                else null

                val index = if (labelValue != null) labels.indexOf(labelValue) else -1
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

        private fun getNumber(reader: JsonReader): Number? {
            val stringNumber = reader.nextString()
            return stringNumber.toSupportedTypeOrNull(labelType) as? Number
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
            if (adapter != fallbackAdapter && !subTypeIncludeLabelKey) {
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
                else -> throw IllegalArgumentException("${value.javaClass.simpleName} is not supported")
            }
        }
    }
}

