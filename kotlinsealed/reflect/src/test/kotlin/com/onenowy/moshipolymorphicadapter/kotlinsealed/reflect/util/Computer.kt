package com.onenowy.moshipolymorphicadapter.kotlinsealed.reflect.util

import com.onenowy.moshipolymorphicadapter.PolymorphicAdapterType
import com.onenowy.moshipolymorphicadapter.annotations.DefaultNull
import com.onenowy.moshipolymorphicadapter.annotations.NameLabel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true, generator = PolymorphicAdapterType.NAME_ADAPTER)
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


