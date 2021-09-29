/*
 * Copyright (c) 2021 nowy(nowy08 at gmail dot com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onenowy.moshipolymorphicadapter

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