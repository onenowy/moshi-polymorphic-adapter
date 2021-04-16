package com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory

fun String.toSupportedTypeOrNull(type: SupportValueType): Any? {
    return this.let {
        when (type) {
            SupportValueType.STRING -> it
            SupportValueType.BOOLEAN -> {
                val str = it.trim()
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