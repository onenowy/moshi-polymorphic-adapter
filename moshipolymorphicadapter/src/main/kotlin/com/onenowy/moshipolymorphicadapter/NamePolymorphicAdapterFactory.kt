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

import com.squareup.moshi.*
import java.lang.reflect.Type


/**
 * A polymorphic JSONAdapter
 *
 */
class NamePolymorphicAdapterFactory<T> @JvmOverloads constructor(
    private val baseType: Class<T>,
    private val subTypes: List<Type> = emptyList(),
    private val nameLabels: List<String> = emptyList(),
    private val fallbackAdapter: JsonAdapter<Any>? = null
) : MoshiPolymorphicAdapterFactory<NamePolymorphicAdapterFactory<T>, T> {

    override fun create(
        type: Type,
        annotations: MutableSet<out Annotation>,
        moshi: Moshi
    ): JsonAdapter<*>? {
        if (Types.getRawType(type) != baseType || annotations.isNotEmpty()) {
            return null
        }
        val jsonAdapters: List<JsonAdapter<Any>> = subTypes.map { moshi.adapter(it) }
        return NameAdapter(subTypes, nameLabels, jsonAdapters, fallbackAdapter).nullSafe()
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
     * @param nameLabel The unique property name of subtype
     */
    fun withSubtype(subType: Class<out T>, nameLabel: String): NamePolymorphicAdapterFactory<T> {
        require(!nameLabels.contains(nameLabel)) { "$nameLabel must be unique" }
        val newSubTypes = subTypes.toMutableList()
        newSubTypes.add(subType)
        val newNameLabels = nameLabels.toMutableList()
        newNameLabels.add(nameLabel)
        return NamePolymorphicAdapterFactory(baseType, newSubTypes, newNameLabels, fallbackAdapter)
    }

    fun withSubtypes(
        subTypes: List<Class<out T>>,
        nameLabels: List<String>
    ): NamePolymorphicAdapterFactory<T> {
        require(nameLabels.size == nameLabels.distinct().size) { "Label Field name for ${baseType.simpleName} must be unique" }
        require(nameLabels.size == subTypes.size) { "The number of Label Field names for ${baseType.simpleName} is different from subtypes" }
        val newSubTypes = this.subTypes.toMutableList()
        newSubTypes.addAll(subTypes)
        val newNameLabels = this.nameLabels.toMutableList()
        newNameLabels.addAll(nameLabels)
        return NamePolymorphicAdapterFactory(baseType, newSubTypes, newNameLabels, fallbackAdapter)
    }

    override fun withFallbackJsonAdapter(fallbackJsonAdapter: JsonAdapter<Any>): NamePolymorphicAdapterFactory<T> {
        return NamePolymorphicAdapterFactory(baseType, subTypes, nameLabels, fallbackJsonAdapter)
    }

    override fun withDefaultValue(defaultValue: T?): NamePolymorphicAdapterFactory<T> {
        return withFallbackJsonAdapter(buildFallbackJsonAdapter(defaultValue))
    }

    class NameAdapter @JvmOverloads constructor(
        private val subTypes: List<Type>,
        private val nameLabels: List<String>,
        private val jsonAdapters: List<JsonAdapter<Any>>,
        private val fallbackAdapter: JsonAdapter<Any>?,
        private val nameOptions: JsonReader.Options = JsonReader.Options.of(*nameLabels.toTypedArray())
    ) : JsonAdapter<Any>() {

        override fun fromJson(reader: JsonReader): Any? {
            val peeked = reader.peekJson()
            peeked.setFailOnUnknown(false)
            val labelIndex = labelIndex(peeked)
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
                val index = reader.selectName(nameOptions)
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