package com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations

import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.SupportValueType

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ValuePolymorphicAdapter(
    val labelType: SupportValueType,
    val labelKey: String,
    val subTypeIncludeLabelKey: Boolean = false
)