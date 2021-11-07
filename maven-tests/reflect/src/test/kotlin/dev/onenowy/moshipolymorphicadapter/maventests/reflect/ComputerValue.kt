package dev.onenowy.moshipolymorphicadapter.maventests.reflect

import com.squareup.moshi.JsonClass
import dev.onenowy.moshipolymorphicadapter.PolymorphicAdapterType
import dev.onenowy.moshipolymorphicadapter.annotations.ValueLabel

@JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_INT + ":type")
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


