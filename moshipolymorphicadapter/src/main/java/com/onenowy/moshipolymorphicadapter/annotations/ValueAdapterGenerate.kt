package com.onenowy.moshipolymorphicadapter.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ValueAdapterGenerate(val labelType: KClass<out Any>, val labelKey: String)
