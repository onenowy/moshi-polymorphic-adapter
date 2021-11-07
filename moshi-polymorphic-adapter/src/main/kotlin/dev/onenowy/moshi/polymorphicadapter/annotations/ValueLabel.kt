@file:Suppress("KDocUnresolvedReference")

package dev.onenowy.moshi.polymorphicadapter.annotations

/**
 * An annotation sets the value as a label for [ValuePolymorphicAdapter].
 *
 * @property value the unique value that determines type.
 *
 * If each target subclass is set with a unique value, each subclass can be de- and serialized, it doesn't have to
 * include [{typeLabel}], which is a JSON field name that is set in [generator] with [PolymorphicAdapterType] in [@JsonClass].
 *
 * ```
 * [
 *  {
 *      "type":1,
 *      "typeOneData":"test"
 *  },
 *  {
 *      "type":2,
 *      "typeTwoData": 1
 *  }
 * ]
 * ```
 *
 * ```
 * @JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_INT + ":" + "type")
 * sealed class Parent()
 *
 * @ValueLabel(1.toString())
 * data class FirstChild(val type:Int, val typeOneData:String):Parent()
 *
 * @ValueLabel(2.toString())
 * class SecondChild(val typeTwoData:Int):Parent()
 * ```
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ValueLabel(val value: String)
