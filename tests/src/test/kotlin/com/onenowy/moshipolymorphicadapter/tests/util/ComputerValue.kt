package com.onenowy.moshipolymorphicadapter.tests.util

import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.SupportValueType
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.ValueLabel
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.ValuePolymorphicAdapter
import com.squareup.moshi.JsonClass

@ValuePolymorphicAdapter(SupportValueType.INT, "type")
sealed class ComputerValue

@ValueLabel(1.toString())
@JsonClass(generateAdapter = true)
data class MonitorValue(val monitor: Int?, val testValue: String) : ComputerValue()

@ValueLabel(2.toString())
@JsonClass(generateAdapter = true)
data class MouseValue(val mouse: String?, val testValue: String) : ComputerValue()

@ValueLabel(3.toString())
@JsonClass(generateAdapter = true)
data class KeyboardValue(val keyboard: Boolean?, val testValue: String) : ComputerValue()


