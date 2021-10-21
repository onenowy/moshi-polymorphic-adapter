@file:Suppress("KDocUnresolvedReference")

package com.onenowy.moshipolymorphicadapter.annotations

/**
 * An annotation sets the value as a label for [ValuePolymorphicAdapter].
 *
 * @property value the unique value that determines type.
 *
 * If each target subclass is set with a unique value, each subclass can be de- and serialized, it doesn't even
 * have [{labelKey}], which is a JSON name that is set as [generator] with [PolymorphicAdapterType] in [@JsonClass].
 *
 * ```
 * [
 *  {
 *      "type":1,
 *      "commonData":1
 *  },
 *  {
 *      "type":2,
 *      "commonData":1
 *  }
 * ]
 * ```
 *
 * ```
 * @JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_INT + ":type")
 * sealed class Parent()
 *
 * @ValueLabel(1.toString())
 * data class FirstChild(val type:Int, val commonData:String):Parent()
 *
 * @ValueLabel(2.toString())
 * class SecondChild(val commonData:String):Parent()
 * ```
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ValueLabel(val value: String)
