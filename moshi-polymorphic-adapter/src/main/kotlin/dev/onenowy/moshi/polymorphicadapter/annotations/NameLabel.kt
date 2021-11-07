@file:Suppress("KDocUnresolvedReference")

package dev.onenowy.moshi.polymorphicadapter.annotations

/**
 * An annotation sets a JSON field name as a label for [NamePolymorphicAdapter].
 *
 * @property name the unique JSON field name.
 *
 * ```
 * [
 *  {
 *      "unique Name":1,
 *      "commonData":"data",
 *      "data" : "data"
 *  },
 * {
 *      "uniqueSecondName":1,
 *      "commonData":"data",
 *      "data": 1
 *  }
 * ]
 * ```
 *
 * ```
 * @JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.NAME_POLYMORPHIC_ADAPTER)
 * sealed class Parent()
 *
 * @NameLabel("unique Name")
 * data class FirstChild(@Json(name = "unique name") val uniqueName:Int, val commonData:Sting, val data:String):Parent()
 *
 * @NameLabel("uniqueSecondName")
 * data class SecondChild(val uniqueSecondName:Int, val commonData:Sting, val data:Int):Parent()
 * ```
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class NameLabel(val name: String)
