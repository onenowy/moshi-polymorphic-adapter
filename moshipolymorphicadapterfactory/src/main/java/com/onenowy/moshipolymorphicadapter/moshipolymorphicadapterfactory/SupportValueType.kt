package com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory

enum class SupportValueType(val classType: Class<*>) {
    BYTE(Byte::class.javaObjectType),
    SHORT(Short::class.javaObjectType),
    INT(Int::class.javaObjectType),
    LONG(Long::class.javaObjectType),
    FLOAT(Float::class.javaObjectType),
    DOUBLE(Double::class.javaObjectType),
    CHAR(Char::class.javaObjectType),
    STRING(String::class.javaObjectType),
    BOOLEAN(Boolean::class.javaObjectType)
}