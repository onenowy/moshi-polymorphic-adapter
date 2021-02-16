package com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValueAdaterFactoryReflection(val labelType: KClass<out Any>, val labelKey: String)
