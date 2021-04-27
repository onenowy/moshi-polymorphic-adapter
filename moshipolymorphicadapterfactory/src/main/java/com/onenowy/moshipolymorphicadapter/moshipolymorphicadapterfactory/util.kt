package com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory

fun String.toSupportedTypeOrNull(type: SupportValueType): Any? {
    return this.trim().let {
        when (type) {
            SupportValueType.STRING -> it
            SupportValueType.CHAR -> {
                val chars = it.toCharArray()
                return if (chars.size == 1) {
                    chars.first()
                } else {
                    null
                }
            }
            SupportValueType.BOOLEAN -> {
                val str = it
                if (str == "true" || str == "false") it == "true" else null
            }
            SupportValueType.BYTE -> toByteOrNull()
            SupportValueType.SHORT -> toShortOrNull()
            SupportValueType.INT -> toIntOrNull()
            SupportValueType.LONG -> toLongOrNull()
            SupportValueType.FLOAT -> toFloatOrNull()
            SupportValueType.DOUBLE -> toDoubleOrNull()
        }
    }
}

fun Any.typeCheck(type: SupportValueType): Boolean {
    return this::class.javaObjectType == type.classType
}