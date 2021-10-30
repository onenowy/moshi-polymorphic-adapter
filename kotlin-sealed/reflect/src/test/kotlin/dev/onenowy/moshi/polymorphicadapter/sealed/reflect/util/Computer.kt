package dev.onenowy.moshi.polymorphicadapter.sealed.reflect.util

import com.squareup.moshi.JsonClass
import dev.onenowy.moshi.polymorphicadapter.PolymorphicAdapterType
import dev.onenowy.moshi.polymorphicadapter.annotations.DefaultNull
import dev.onenowy.moshi.polymorphicadapter.annotations.NameLabel

@JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.NAME_POLYMORPHIC_ADAPTER)
@DefaultNull
sealed class Computer

@JsonClass(generateAdapter = true)
@NameLabel("monitorUnique")
data class Monitor(val monitorUnique: Int?, val testValue: String) : Computer()

@JsonClass(generateAdapter = true)
@NameLabel("mouseUnique")
data class Mouse(val mouseUnique: String?, val testValue: String) : Computer()

@JsonClass(generateAdapter = true)
@NameLabel("keyboardUnique")
data class Keyboard(val keyboardUnique: Boolean?, val testValue: String) : Computer()


