package com.onenowy.moshi.polymorphicadapter.sealed.reflect.util

import com.onenowy.moshi.polymorphicadapter.PolymorphicAdapterType
import com.onenowy.moshi.polymorphicadapter.annotations.DefaultNull
import com.onenowy.moshi.polymorphicadapter.annotations.NameLabel
import com.squareup.moshi.JsonClass

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


