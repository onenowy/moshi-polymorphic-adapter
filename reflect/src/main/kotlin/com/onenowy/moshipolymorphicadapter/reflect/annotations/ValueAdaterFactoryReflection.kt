package com.onenowy.moshipolymorphicadapter.reflect.annotations

import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.SupportValueType

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValueAdaterFactoryReflection(val labelType: SupportValueType, val labelKey: String)
