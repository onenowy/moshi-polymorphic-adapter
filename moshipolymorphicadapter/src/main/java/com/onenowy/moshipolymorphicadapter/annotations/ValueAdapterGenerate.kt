package com.onenowy.moshipolymorphicadapter.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValueAdapterGenerate(val labelType: KClass<out Any>, val labelKey: String)
