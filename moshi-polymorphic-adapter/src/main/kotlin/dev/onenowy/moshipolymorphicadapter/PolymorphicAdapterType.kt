@file:Suppress("KDocUnresolvedReference")

package dev.onenowy.moshipolymorphicadapter

/**
 * Use [PolymorphicAdapterType] to specify adapter type generated by reflection or annotation processor with [generator] value in [@JsonClass].
 * ```
 * @JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.NAME_ADAPTER)
 * ```
 * For [ValuePolymorphicAdapter], [:{labelKey}], a JSON name that determines type, is required with [PolymorphicAdapterType].
 * ```
 * @JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_INT + ":labelKey")
 * ```
 */

object PolymorphicAdapterType {
    const val NAME_POLYMORPHIC_ADAPTER = "MoshiPolymorphic.Name"
    const val VALUE_POLYMORPHIC_ADAPTER_INT = "MoshiPolymorphic.Int"
    const val VALUE_POLYMORPHIC_ADAPTER_LONG = "MoshiPolymorphic.Long"
    const val VALUE_POLYMORPHIC_ADAPTER_DOUBLE = "MoshiPolymorphic.Double"
    const val VALUE_POLYMORPHIC_ADAPTER_STRING = "MoshiPolymorphic.String"
    const val VALUE_POLYMORPHIC_ADAPTER_BOOLEAN = "MoshiPolymorphic.Boolean"
}

fun String.toSupportedTypeValueOrNull(type: String): Any? {
    return this.trim().let {
        when (type) {
            PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_STRING -> it
            PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_BOOLEAN -> toBooleanStrictOrNull()
            PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_INT -> toIntOrNull()
            PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_LONG -> toLongOrNull()
            PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_DOUBLE -> toDoubleOrNull()
            else -> null
        }
    }
}

fun <T> getValueAdapterTypeOrNull(type: Class<T>): String? {
    return when (type) {
        String::class.java -> PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_STRING
        Boolean::class.javaObjectType, Boolean::class.javaPrimitiveType -> PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_BOOLEAN
        Int::class.javaObjectType, Int::class.javaPrimitiveType -> PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_INT
        Long::class.javaObjectType, Long::class.javaPrimitiveType -> PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_LONG
        Double::class.javaObjectType, Double::class.javaPrimitiveType -> PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_DOUBLE
        else -> null
    }
}

fun getSupportedTypeClass(type: String) = when (type) {
    PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_INT -> Int::class
    PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_DOUBLE -> Double::class
    PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_BOOLEAN -> Boolean::class
    PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_LONG -> Long::class
    PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_STRING -> String::class
    else -> throw IllegalArgumentException("")
}