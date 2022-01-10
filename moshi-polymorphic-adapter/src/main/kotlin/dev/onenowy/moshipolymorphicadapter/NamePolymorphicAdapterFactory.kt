package dev.onenowy.moshipolymorphicadapter

import com.squareup.moshi.*
import java.lang.reflect.Type

/**
 * A polymorphic adapter factory creates an adapter that uses the unique JSON field name
 * to determine which type to decode to.
 */
class NamePolymorphicAdapterFactory<T> @JvmOverloads internal constructor(
    private val baseType: Class<T>,
    private val subTypes: List<Type> = emptyList(),
    private val nameLabels: List<String> = emptyList(),
    private val fallbackAdapter: JsonAdapter<Any>? = null
) : AbstractMoshiPolymorphicAdapterFactory<NamePolymorphicAdapterFactory<T>, T>() {

    override fun create(
        type: Type,
        annotations: Set<Annotation>,
        moshi: Moshi
    ): JsonAdapter<*>? {
        if (Types.getRawType(type) != baseType || annotations.isNotEmpty()) {
            return null
        }
        val jsonAdapters: List<JsonAdapter<Any>> = subTypes.map(moshi::adapter)
        return NamePolymorphicAdapter(subTypes, nameLabels, jsonAdapters, fallbackAdapter).nullSafe()
    }

    companion object {
        /**
         * @param baseType The base type for which this factory will create adapters.
         */
        @JvmStatic
        fun <T> of(baseType: Class<T>): NamePolymorphicAdapterFactory<T> {
            return NamePolymorphicAdapterFactory(baseType)
        }
    }

    /**
     * Returns a new factory that decodes instances of [subType]
     */
    fun withSubtype(subType: Class<out T>, nameLabel: String): NamePolymorphicAdapterFactory<T> {
        require(!nameLabels.contains(nameLabel)) { "$nameLabel must be unique" }
        val newSubTypes = buildList {
            addAll(subTypes)
            add(subType)
        }
        val newNameLabels = buildList {
            addAll(nameLabels)
            add(nameLabel)
        }
        return NamePolymorphicAdapterFactory(baseType, newSubTypes, newNameLabels, fallbackAdapter)
    }

    /**
     * This method is similar to [withSubtype], but it gets lists as arguments.
     * the index of each subtype corresponds to the index of the name label of each subtype.
     */
    fun withSubtypes(
        subTypes: List<Class<out T>>,
        nameLabels: List<String>
    ): NamePolymorphicAdapterFactory<T> {
        require(nameLabels.size == nameLabels.distinct().size) { "The label name for ${baseType.simpleName} must be unique" }
        require(nameLabels.size == subTypes.size) { "The number of label names for ${baseType.simpleName} is different from subtypes" }
        val newSubTypes = buildList {
            addAll(this@NamePolymorphicAdapterFactory.subTypes)
            addAll(subTypes)
        }
        val newNameLabels = buildList {
            addAll(this@NamePolymorphicAdapterFactory.nameLabels)
            addAll(nameLabels)
        }
        return NamePolymorphicAdapterFactory(baseType, newSubTypes, newNameLabels, fallbackAdapter)
    }

    override fun withFallbackJsonAdapter(fallbackJsonAdapter: JsonAdapter<Any>): NamePolymorphicAdapterFactory<T> {
        return NamePolymorphicAdapterFactory(baseType, subTypes, nameLabels, fallbackJsonAdapter)
    }

    class NamePolymorphicAdapter(
        private val subTypes: List<Type>,
        private val nameLabels: List<String>,
        private val jsonAdapters: List<JsonAdapter<Any>>,
        private val fallbackAdapter: JsonAdapter<Any>?
    ) : JsonAdapter<Any>() {

        private val nameOptions: JsonReader.Options = JsonReader.Options.of(*nameLabels.toTypedArray())

        override fun fromJson(reader: JsonReader): Any? {
            val peeked = reader.peekJson()
            peeked.setFailOnUnknown(false)
            val labelIndex = peeked.use(::labelIndex)
            return if (labelIndex == -1) {
                if (fallbackAdapter != null) {
                    fallbackAdapter.fromJson(reader)
                } else {
                    throw JsonDataException("No matching Field names for $nameLabels")
                }
            } else {
                jsonAdapters[labelIndex].fromJson(reader)
            }
        }

        private fun labelIndex(reader: JsonReader): Int {
            reader.beginObject()
            while (reader.hasNext()) {
                val labelIndex = reader.selectName(nameOptions)
                if (labelIndex == -1) {
                    reader.skipName()
                    reader.skipValue()
                    continue
                }
                return labelIndex
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
                requireNotNull(fallbackAdapter) { "Expected one of $subTypes but found $value, a ${type}. Register this subtype." }
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