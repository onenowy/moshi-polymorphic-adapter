@file:Suppress("KDocUnresolvedReference")

package dev.onenowy.moshi.polymorphicadapter.annotations

/**
 * An annotation sets property name as a label for [NamePolymorphicAdapter].
 *
 * @property name the unique property name.
 *
 * ```
 * [
 *  {
 *      "uniqueData":1,
 *      "commonData":"data"
 *  },
 * {
 *      "uniqueSecondData":1,
 *      "commonData":"data"
 *  }
 * ]
 * ```
 *
 * ```
 * @JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.NAME_ADAPTER)
 * sealed class Parent()
 *
 * @NameLabel("uniqueData")
 * data class FirstChild(val uniqueData:Int, val commonData:Sting):Parent()
 *
 * @NameLabel("uniqueSecondData")
 * data class SecondChild(val uniqueSecondData:Int, val commonData:Sting):Parent()
 * ```
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class NameLabel(val name: String)
