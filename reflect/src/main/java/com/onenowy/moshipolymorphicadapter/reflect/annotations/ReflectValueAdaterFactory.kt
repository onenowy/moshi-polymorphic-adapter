package com.onenowy.moshipolymorphicadapter.reflect.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ReflectValueAdaterFactory(val labelType: KClass<out Any>, val labelKey: String)
