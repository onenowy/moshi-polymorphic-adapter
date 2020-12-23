package com.onenowy.moshipolymorphicadapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.lang.reflect.Type

abstract class AbstractMoshiPolymorphicAdapterFactory<S, T>(
    protected val baseType: Class<T>,
    protected val subTypes: List<Type> = emptyList(),
    protected val fallbackAdapter: JsonAdapter<Any>? = null
) : JsonAdapter.Factory {

    internal fun buildFallbackJsonAdapter(defaultValue: T?): JsonAdapter<Any> {
        return object : JsonAdapter<Any>() {
            override fun fromJson(reader: JsonReader): Any? {
                reader.skipValue()
                return defaultValue
            }

            override fun toJson(writer: JsonWriter, value: Any?) {
                throw IllegalArgumentException("Expected one of $subTypes but found $value, a ${value?.javaClass}. Register this subtype.")
            }
        }
    }

    abstract fun withFallbackJsonAdapter(fallbackJsonAdapter: JsonAdapter<Any>): S

    abstract fun withDefaultValue(defaultValue: T?): S
}