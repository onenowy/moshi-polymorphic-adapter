package com.onenowy.moshipolymorphicadapter.tests.util

import com.onenowy.moshipolymorphicadapter.codegen.annotations.ValueAdaterFactoryCodegen
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.SupportValueType
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.LabelValue
import com.squareup.moshi.JsonClass

@ValueAdaterFactoryCodegen(SupportValueType.INT, "type")
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


