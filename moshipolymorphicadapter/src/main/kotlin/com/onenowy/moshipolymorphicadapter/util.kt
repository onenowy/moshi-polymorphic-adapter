package com.onenowy.moshipolymorphicadapter

fun String.toSupportTypeOrNull(type: String): Any? {
    return this.trim().let {
        when (type) {
            PolymorphicAdapterType.ValueAdapter.STRING -> it
            PolymorphicAdapterType.ValueAdapter.BOOLEAN -> toBooleanStrictOrNull()
            PolymorphicAdapterType.ValueAdapter.INT -> toIntOrNull()
            PolymorphicAdapterType.ValueAdapter.LONG -> toLongOrNull()
            PolymorphicAdapterType.ValueAdapter.DOUBLE -> toDoubleOrNull()
            else -> null
        }
    }
}

fun <T> getValueAdaterTypeOrNull(type: Class<T>): String? {
    return when (type) {
        String::class.java -> PolymorphicAdapterType.ValueAdapter.STRING
        Boolean::class.javaObjectType, Boolean::class.javaPrimitiveType -> PolymorphicAdapterType.ValueAdapter.BOOLEAN
        Int::class.javaObjectType, Int::class.javaPrimitiveType -> PolymorphicAdapterType.ValueAdapter.INT
        Long::class.javaObjectType, Long::class.javaPrimitiveType -> PolymorphicAdapterType.ValueAdapter.LONG
        Double::class.javaObjectType, Double::class.javaPrimitiveType -> PolymorphicAdapterType.ValueAdapter.DOUBLE
        else -> null
    }
}

fun getSupportTypeClass(type: String) = when (type) {
    PolymorphicAdapterType.ValueAdapter.INT -> Int::class
    PolymorphicAdapterType.ValueAdapter.DOUBLE -> Double::class
    PolymorphicAdapterType.ValueAdapter.BOOLEAN -> Boolean::class
    PolymorphicAdapterType.ValueAdapter.LONG -> Long::class
    PolymorphicAdapterType.ValueAdapter.STRING -> String::class
    else -> throw IllegalArgumentException("")
}