package dev.onenowy.moshipolymorphicadapter.sealed.reflect

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import dev.onenowy.moshipolymorphicadapter.PolymorphicAdapterType
import dev.onenowy.moshipolymorphicadapter.annotations.DefaultNull
import dev.onenowy.moshipolymorphicadapter.annotations.NameLabel

@JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.NAME_POLYMORPHIC_ADAPTER)
@DefaultNull
sealed class Computer

@JsonClass(generateAdapter = true)
@NameLabel("monitor_Unique")
data class Monitor(@Json(name = "monitor_Unique") val monitorUnique: Int?, val testValue: String) : Computer()

@JsonClass(generateAdapter = true)
@NameLabel("mouseUnique")
data class Mouse(val mouseUnique: String?, val testValue: String) : Computer()

@JsonClass(generateAdapter = true)
@NameLabel("keyboardUnique")
data class Keyboard(val keyboardUnique: Boolean?, val testValue: String) : Computer()


