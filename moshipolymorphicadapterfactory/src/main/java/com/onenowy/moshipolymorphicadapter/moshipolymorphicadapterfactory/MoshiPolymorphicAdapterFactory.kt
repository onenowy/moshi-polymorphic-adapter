package com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

interface MoshiPolymorphicAdapterFactory<S : MoshiPolymorphicAdapterFactory<S, T>, T> : JsonAdapter.Factory {

    fun buildFallbackJsonAdapter(defaultValue: T?): JsonAdapter<Any> {
        return object : JsonAdapter<Any>() {
            override fun fromJson(reader: JsonReader): Any? {
                reader.skipValue()
                return defaultValue
            }

            override fun toJson(writer: JsonWriter, value: Any?) {
                throw IllegalArgumentException("FallbackJsonAdapter with $defaultValue cannot make Json Object")
            }
        }
    }

    fun withFallbackJsonAdapter(fallbackJsonAdapter: JsonAdapter<Any>): S

    fun withDefaultValue(defaultValue: T?): S
}