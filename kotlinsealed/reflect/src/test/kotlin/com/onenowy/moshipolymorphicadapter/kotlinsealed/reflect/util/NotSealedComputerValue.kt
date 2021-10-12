package com.onenowy.moshipolymorphicadapter.kotlinsealed.reflect.util

import com.onenowy.moshipolymorphicadapter.PolymorphicAdapterType
import com.onenowy.moshipolymorphicadapter.annotations.ValueLabel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.VALUE_ADAPTER_INT + ":type")
interface NotSealedComputerValue

@ValueLabel(1.toString())
@JsonClass(generateAdapter = true)
data class MonitorNotSealedValue(val monitor: Int?, val testValue: String) : NotSealedComputerValue

@ValueLabel(2.toString())
@JsonClass(generateAdapter = true)
data class MouseNotSealedValue(val mouse: String?, val testValue: String) : NotSealedComputerValue



