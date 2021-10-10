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

fun <T> getValueAdaterTypeOrNull(type: Class<T>): String? {
    return when (type) {
        String::class.java -> AdapterType.VALUE_ADAPTER.STRING
        Boolean::class.javaObjectType, Boolean::class.javaPrimitiveType -> AdapterType.VALUE_ADAPTER.BOOLEAN
        Int::class.javaObjectType, Int::class.javaPrimitiveType -> AdapterType.VALUE_ADAPTER.INT
        Long::class.javaObjectType, Long::class.javaPrimitiveType -> AdapterType.VALUE_ADAPTER.LONG
        Double::class.javaObjectType, Double::class.javaPrimitiveType -> AdapterType.VALUE_ADAPTER.DOUBLE
        else -> null
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