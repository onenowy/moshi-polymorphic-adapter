package com.onenowy.moshipolymorphicadapter.util

import com.onenowy.moshipolymorphicadapter.annotations.LabelValue
import com.onenowy.moshipolymorphicadapter.annotations.ValueAdapterGenerate
import com.squareup.moshi.JsonClass

@ValueAdapterGenerate(Int::class, "type")
sealed class ComputerValue

@LabelValue(1.toString())
@JsonClass(generateAdapter = true)
data class MonitorValue(val monitor: Int?) : ComputerValue()

@LabelValue(2.toString())
@JsonClass(generateAdapter = true)
data class MouseValue(val mouse: String?) : ComputerValue()

@LabelValue(3.toString())
@JsonClass(generateAdapter = true)
data class KeyboardValue(val keyboard: Boolean?) : ComputerValue()


