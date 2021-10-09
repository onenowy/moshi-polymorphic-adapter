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