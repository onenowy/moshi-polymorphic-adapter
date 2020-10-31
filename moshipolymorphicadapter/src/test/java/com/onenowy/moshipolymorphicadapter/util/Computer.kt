package com.onenowy.moshipolymorphicadapter.util

import com.squareup.moshi.JsonClass

sealed class Computer(val type: ComType) {
    enum class ComType(val value: Int) {
        Monitor(1), Mouse(2), Keyboard(3)
    }
}


@JsonClass(generateAdapter = true)
data class Monitor(val monitorUnique: Int?) : Computer(ComType.Monitor)

@JsonClass(generateAdapter = true)
data class Mouse(val mouseUnique: String?) : Computer(ComType.Mouse)

@JsonClass(generateAdapter = true)
data class Keyboard(val keyboardUnique: Boolean?) : Computer(ComType.Keyboard)