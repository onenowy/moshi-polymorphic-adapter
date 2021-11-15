package dev.onenowy.moshipolymorphicadapter.maventests.reflect

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import dev.onenowy.moshipolymorphicadapter.PolymorphicAdapterType
import dev.onenowy.moshipolymorphicadapter.annotations.DefaultNull
import dev.onenowy.moshipolymorphicadapter.annotations.NameLabel

@JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.NAME_POLYMORPHIC_ADAPTER)
@DefaultNull
sealed class ComputerReflect

@NameLabel("monitor_Unique")
data class MonitorReflect(@Json(name = "monitor_Unique") val monitorUnique: Int?, val testValue: String) : ComputerReflect()

@NameLabel("mouseUnique")
data class MouseReflect(val mouseUnique: String?, val testValue: String) : ComputerReflect()

@NameLabel("keyboardUnique")
data class KeyboardReflect(val keyboardUnique: Boolean?, val testValue: String) : ComputerReflect()


