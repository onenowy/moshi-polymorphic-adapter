@file:Suppress("KDocUnresolvedReference")

package dev.onenowy.moshipolymorphicadapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

/**
 * [withFallbackJsonAdapter] and [withDefaultValue] are derived from [PolymorphicJsonAdapterFactory] of moshi.
 */
abstract class AbstractMoshiPolymorphicAdapterFactory<S : AbstractMoshiPolymorphicAdapterFactory<S, T>, T> :
    JsonAdapter.Factory {

    private fun buildFallbackJsonAdapter(defaultValue: T?): JsonAdapter<Any> {
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

    /**
     * Returns a new factory that with default to [fallbackJsonAdapter.fromJson(reader)] upon
     * decoding of unrecognized labels.
     */

    abstract fun withFallbackJsonAdapter(fallbackJsonAdapter: JsonAdapter<Any>): S

    /**
     * Returns a new factory that will default to [defaultValue] upon decoding of unrecognized
     * labels. The default value should be immutable.
     */
    fun withDefaultValue(defaultValue: T?) = withFallbackJsonAdapter((buildFallbackJsonAdapter(defaultValue)))
}