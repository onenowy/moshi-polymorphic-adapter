package com.onenowy.moshipolymorphicadapter

fun <T> String.toSupportedTypeOrNull(type: Class<T>): T? {
    return this.let {
        when (type) {
            String::class.java -> it
            Boolean::class.javaPrimitiveType, Boolean::class.javaObjectType -> {
                val str = it.trim()
                if (str == "true" || str == "false") it == "true" else null
            }
            Byte::class.javaPrimitiveType, Byte::class.javaObjectType -> toByteOrNull()
            Short::class.javaPrimitiveType, Short::class.javaObjectType -> toShortOrNull()
            Int::class.javaPrimitiveType, Int::class.javaObjectType -> toIntOrNull()
            Long::class.javaPrimitiveType, Long::class.javaObjectType -> toLongOrNull()
            Float::class.javaPrimitiveType, Float::class.javaObjectType -> toFloatOrNull()
            Double::class.javaPrimitiveType, Double::class.javaObjectType -> toDoubleOrNull()
            else -> null
        } as? T
    }
}