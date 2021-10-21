package com.onenowy.moshi.polymorphicadapter.sealed.reflect.util

import com.onenowy.moshi.polymorphicadapter.PolymorphicAdapterType
import com.onenowy.moshi.polymorphicadapter.annotations.ValueLabel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_INT + ":type")
interface NotSealedComputerValue

@ValueLabel(1.toString())
@JsonClass(generateAdapter = true)
data class MonitorNotSealedValue(val monitor: Int?, val testValue: String) : NotSealedComputerValue

@ValueLabel(2.toString())
@JsonClass(generateAdapter = true)
data class MouseNotSealedValue(val mouse: String?, val testValue: String) : NotSealedComputerValue



