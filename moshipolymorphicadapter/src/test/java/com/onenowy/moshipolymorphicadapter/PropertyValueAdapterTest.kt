package com.onenowy.moshipolymorphicadapter

import com.google.common.truth.Truth
import com.onenowy.moshipolymorphicadapter.util.Computer
import com.onenowy.moshipolymorphicadapter.util.Keyboard
import com.onenowy.moshipolymorphicadapter.util.Monitor
import com.onenowy.moshipolymorphicadapter.util.Mouse
import com.squareup.moshi.Moshi
import org.junit.Test

class PropertyValueAdapterTest {
    private val moshiBuilder = Moshi.Builder().add(
        PropertyValueAdapterFactory.of(Computer::class.java, "type", Int::class.javaObjectType)
            .withSubType(Monitor::class.java, Computer.ComType.Monitor.value)
            .withSubType(Keyboard::class.java, Computer.ComType.Keyboard.value).withSubType(Mouse::class.java, Computer.ComType.Mouse.value)
    )

    private val monitor = Monitor(1)
    private val mouse = Mouse("mouse")
    private val keyboard = Keyboard(true)

    private val monitorJson = "{\"type\":1,\"monitorUnique\":1}"
    private val mouseJson = "{\"type\":2,\"mouseUnique\":\"mouse\"}"
    private val keyboardJson = "{\"type\":3,\"keyboardUnique\":true}"

    @Test
    fun toJson() {
        val moshi = moshiBuilder.build()
        val adapter = moshi.adapter(Computer::class.java)
        Truth.assertThat(adapter.toJson(monitor)).isEqualTo(monitorJson)
        Truth.assertThat(adapter.toJson(mouse)).isEqualTo(mouseJson)
        Truth.assertThat(adapter.toJson(keyboard)).isEqualTo(keyboardJson)
    }

    @Test
    fun fromJson() {
        val moshi = moshiBuilder.build()
        val adapter = moshi.adapter(Computer::class.java)
        Truth.assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor)
        Truth.assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse)
        Truth.assertThat(adapter.fromJson(keyboardJson)).isEqualTo(keyboard)
    }

}