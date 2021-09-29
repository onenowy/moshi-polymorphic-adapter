package com.onenowy.moshipolymorphicadapter.annotations

import com.onenowy.moshipolymorphicadapter.SupportValueType

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ValuePolymorphicAdapter(
    val labelType: SupportValueType,
    val labelKey: String,
    val subTypeIncludeLabelKey: Boolean = false
)