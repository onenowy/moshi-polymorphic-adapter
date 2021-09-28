package com.onenowy.moshipolymorphicadapter.reflect.util

import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.SupportValueType
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.ValueLabel
import com.onenowy.moshipolymorphicadapter.reflect.annotations.ValueAdaterFactoryReflection
import com.squareup.moshi.JsonClass

@ValueAdaterFactoryReflection(SupportValueType.INT, "type")
sealed class ComputerValue

@ValueLabel(1.toString())
@JsonClass(generateAdapter = true)
data class MonitorValue(val monitor: Int?) : ComputerValue()

@ValueLabel(2.toString())
@JsonClass(generateAdapter = true)
data class MouseValue(val mouse: String?) : ComputerValue()

@ValueLabel(3.toString())
@JsonClass(generateAdapter = true)
data class KeyboardValue(val keyboard: Boolean?) : ComputerValue()


