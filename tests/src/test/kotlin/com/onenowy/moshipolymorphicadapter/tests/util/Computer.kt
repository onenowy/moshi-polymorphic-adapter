package com.onenowy.moshipolymorphicadapter.tests.util

import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.LabelName
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.annotations.NameAdapterFactoryCodegen
import com.squareup.moshi.JsonClass

@NameAdapterFactoryCodegen
sealed class Computer(val typeInt: ComTypeInt, val typeString: ComTypeString, val typeDouble: ComTypeDouble, val typeLong: ComTypeLong) {
    enum class ComTypeInt(val value: Int) {
        Monitor(1), Mouse(2), Keyboard(3)
    }

    enum class ComTypeString(val value: String) {
        Monitor("1"), Mouse("2"), Keyboard("3")
    }

    enum class ComTypeDouble(val value: Double) {
        Monitor(5.0), Mouse(10000.1), Keyboard(Double.MAX_VALUE)
    }

    enum class ComTypeLong(val value: Long) {
        Monitor(Long.MAX_VALUE - 2), Mouse(Long.MAX_VALUE - 1), Keyboard(Long.MAX_VALUE)
    }
}

@JsonClass(generateAdapter = true)
@LabelName("monitorUnique")
data class Monitor(val monitorUnique: Int?, val testValue: String) :
    Computer(ComTypeInt.Monitor, ComTypeString.Monitor, ComTypeDouble.Monitor, ComTypeLong.Monitor)

@JsonClass(generateAdapter = true)
@LabelName("mouseUnique")
data class Mouse(val mouseUnique: String?, val testValue: String) :
    Computer(ComTypeInt.Mouse, ComTypeString.Mouse, ComTypeDouble.Mouse, ComTypeLong.Mouse)

@JsonClass(generateAdapter = true)
@LabelName("keyboardUnique")
data class Keyboard(val keyboardUnique: Boolean?, val testValue: String) :
    Computer(ComTypeInt.Keyboard, ComTypeString.Keyboard, ComTypeDouble.Keyboard, ComTypeLong.Keyboard)


