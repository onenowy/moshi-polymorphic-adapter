package com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations

import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.SupportValueType

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ValueAdaterFactoryCodegen(val labelType: SupportValueType, val labelKey: String)