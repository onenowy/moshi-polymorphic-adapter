package com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ValueAdaterFactoryCodegen(val labelType: KClass<out Any>, val labelKey: String)
