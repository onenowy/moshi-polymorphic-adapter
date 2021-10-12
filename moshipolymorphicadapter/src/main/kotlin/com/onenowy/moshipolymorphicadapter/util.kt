package com.onenowy.moshipolymorphicadapter

fun String.toSupportTypeOrNull(type: String): Any? {
    return this.trim().let {
        when (type) {
            PolymorphicAdapterType.VALUE_ADAPTER_STRING -> it
            PolymorphicAdapterType.VALUE_ADAPTER_BOOLEAN -> toBooleanStrictOrNull()
            PolymorphicAdapterType.VALUE_ADAPTER_INT -> toIntOrNull()
            PolymorphicAdapterType.VALUE_ADAPTER_LONG -> toLongOrNull()
            PolymorphicAdapterType.VALUE_ADAPTER_DOUBLE -> toDoubleOrNull()
            else -> null
        }
    }
}

fun <T> getValueAdaterTypeOrNull(type: Class<T>): String? {
    return when (type) {
        String::class.java -> PolymorphicAdapterType.VALUE_ADAPTER_STRING
        Boolean::class.javaObjectType, Boolean::class.javaPrimitiveType -> PolymorphicAdapterType.VALUE_ADAPTER_BOOLEAN
        Int::class.javaObjectType, Int::class.javaPrimitiveType -> PolymorphicAdapterType.VALUE_ADAPTER_INT
        Long::class.javaObjectType, Long::class.javaPrimitiveType -> PolymorphicAdapterType.VALUE_ADAPTER_LONG
        Double::class.javaObjectType, Double::class.javaPrimitiveType -> PolymorphicAdapterType.VALUE_ADAPTER_DOUBLE
        else -> null
    }
}

fun getSupportTypeClass(type: String) = when (type) {
    PolymorphicAdapterType.VALUE_ADAPTER_INT -> Int::class
    PolymorphicAdapterType.VALUE_ADAPTER_DOUBLE -> Double::class
    PolymorphicAdapterType.VALUE_ADAPTER_BOOLEAN -> Boolean::class
    PolymorphicAdapterType.VALUE_ADAPTER_LONG -> Long::class
    PolymorphicAdapterType.VALUE_ADAPTER_STRING -> String::class
    else -> throw IllegalArgumentException("")
}