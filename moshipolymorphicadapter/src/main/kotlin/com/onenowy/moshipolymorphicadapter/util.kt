package com.onenowy.moshipolymorphicadapter

fun String.toSupportTypeOrNull(type: String): Any? {
    return this.trim().let {
        when (type) {
            PolymorphicAdapterType.VALUE_ADAPTER.STRING -> it
            PolymorphicAdapterType.VALUE_ADAPTER.BOOLEAN -> toBooleanStrictOrNull()
            PolymorphicAdapterType.VALUE_ADAPTER.INT -> toIntOrNull()
            PolymorphicAdapterType.VALUE_ADAPTER.LONG -> toLongOrNull()
            PolymorphicAdapterType.VALUE_ADAPTER.DOUBLE -> toDoubleOrNull()
            else -> null
        }
    }
}

fun <T> getValueAdaterTypeOrNull(type: Class<T>): String? {
    return when (type) {
        String::class.java -> PolymorphicAdapterType.VALUE_ADAPTER.STRING
        Boolean::class.javaObjectType, Boolean::class.javaPrimitiveType -> PolymorphicAdapterType.VALUE_ADAPTER.BOOLEAN
        Int::class.javaObjectType, Int::class.javaPrimitiveType -> PolymorphicAdapterType.VALUE_ADAPTER.INT
        Long::class.javaObjectType, Long::class.javaPrimitiveType -> PolymorphicAdapterType.VALUE_ADAPTER.LONG
        Double::class.javaObjectType, Double::class.javaPrimitiveType -> PolymorphicAdapterType.VALUE_ADAPTER.DOUBLE
        else -> null
    }
}

fun getSupportTypeClass(type: String) = when (type) {
    PolymorphicAdapterType.VALUE_ADAPTER.INT -> Int::class
    PolymorphicAdapterType.VALUE_ADAPTER.DOUBLE -> Double::class
    PolymorphicAdapterType.VALUE_ADAPTER.BOOLEAN -> Boolean::class
    PolymorphicAdapterType.VALUE_ADAPTER.LONG -> Long::class
    PolymorphicAdapterType.VALUE_ADAPTER.STRING -> String::class
    else -> throw IllegalArgumentException("")
}