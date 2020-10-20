package com.onenowy.moshipolymorphicadapter.util

import com.squareup.moshi.JsonClass

sealed class Computer

@JsonClass(generateAdapter = true)
data class Monitor(val monitorUnique: Int?) : Computer()

@JsonClass(generateAdapter = true)
data class Mouse(val mouseUnique: String?) : Computer()

@JsonClass(generateAdapter = true)
data class Keyboard(val keyboardUnique: Boolean?) : Computer()