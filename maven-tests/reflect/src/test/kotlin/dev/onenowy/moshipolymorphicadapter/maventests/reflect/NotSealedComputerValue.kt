package dev.onenowy.moshipolymorphicadapter.maventests.reflect

import com.squareup.moshi.JsonClass
import dev.onenowy.moshipolymorphicadapter.PolymorphicAdapterType
import dev.onenowy.moshipolymorphicadapter.annotations.ValueLabel

@JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.VALUE_POLYMORPHIC_ADAPTER_INT + ":type")
interface NotSealedComputerValue

@ValueLabel(1.toString())
@JsonClass(generateAdapter = true)
data class MonitorNotSealedValue(val monitor: Int?, val testValue: String) : NotSealedComputerValue

@ValueLabel(2.toString())
@JsonClass(generateAdapter = true)
data class MouseNotSealedValue(val mouse: String?, val testValue: String) : NotSealedComputerValue



