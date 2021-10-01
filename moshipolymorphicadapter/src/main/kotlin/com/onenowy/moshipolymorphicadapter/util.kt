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

fun String.toSupportTypeOrNull(type: String): Any? {
    return this.trim().let {
        when (type) {
            AdapterType.VALUE_ADAPTER.STRING -> it
            AdapterType.VALUE_ADAPTER.BOOLEAN -> toBooleanStrictOrNull()
            AdapterType.VALUE_ADAPTER.INT -> toIntOrNull()
            AdapterType.VALUE_ADAPTER.LONG -> toLongOrNull()
            AdapterType.VALUE_ADAPTER.DOUBLE -> toDoubleOrNull()
            else -> null
        }
    }
}

fun <T> String.toSupportTypeOrNull(type: Class<T>): Any? {
    return this.trim().let {
        when (type) {
            String::class.java -> it
            Boolean::class.javaObjectType, Boolean::class.javaPrimitiveType -> toBooleanStrictOrNull()
            Int::class.javaObjectType, Int::class.javaPrimitiveType -> toIntOrNull()
            Long::class.javaObjectType, Long::class.javaPrimitiveType -> toLongOrNull()
            Double::class.javaObjectType, Double::class.javaPrimitiveType -> toDoubleOrNull()
            else -> null
        }
    }
}

fun getSupportTypeClass(type: String) = when (type) {
    AdapterType.VALUE_ADAPTER.INT -> Int::class
    AdapterType.VALUE_ADAPTER.DOUBLE -> Double::class
    AdapterType.VALUE_ADAPTER.BOOLEAN -> Boolean::class
    AdapterType.VALUE_ADAPTER.LONG -> Long::class
    AdapterType.VALUE_ADAPTER.STRING -> String::class
    else -> throw IllegalArgumentException("")
}