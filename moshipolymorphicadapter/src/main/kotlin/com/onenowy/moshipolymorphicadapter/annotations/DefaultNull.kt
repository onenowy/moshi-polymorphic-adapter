package com.onenowy.moshipolymorphicadapter.annotations

/**
 * [@DefaultNull] indicates that this sealed class uses null as default value for deserialization.
 * ```
 * @JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.NAME_ADAPTER)
 * @DefaultNull
 * sealed class Parent()
 * ```
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class DefaultNull
