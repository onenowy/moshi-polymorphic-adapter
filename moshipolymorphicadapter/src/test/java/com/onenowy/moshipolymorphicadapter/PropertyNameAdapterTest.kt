package com.onenowy.moshipolymorphicadapter

import com.google.common.truth.Truth.assertThat
import com.onenowy.moshipolymorphicadapter.util.Computer
import com.onenowy.moshipolymorphicadapter.util.Keyboard
import com.onenowy.moshipolymorphicadapter.util.Monitor
import com.onenowy.moshipolymorphicadapter.util.Mouse
import com.squareup.moshi.Moshi
import org.junit.Test

class PropertyNameAdapterTest {

    val moshiBuilder = Moshi.Builder().add(
        PropertyNameAdapterFactory.of(Computer::class.java).withSubtype(
            Monitor::class.java,
            "monitorUnique"
        ).withSubtype(Mouse::class.java, "mouseUnique").withSubtype(Keyboard::class.java, "keyboardUnique")
    )

    val monitor = Monitor(1)
    val mouse = Mouse("mouse")
    val keyboard = Keyboard(true)

    val monitorJson = "{\"monitorUnique\":1}"
    val mouseJson = "{\"mouseUnique\":\"mouse\"}"
    val keyboardJson = "{\"keyboardUnique\":true}"


    @Test
    fun toJson() {
        val moshi = moshiBuilder.build()
        val adapter = moshi.adapter(Computer::class.java)
        assertThat(adapter.toJson(monitor)).isEqualTo(monitorJson)
        assertThat(adapter.toJson(mouse)).isEqualTo(mouseJson)
        assertThat(adapter.toJson(keyboard)).isEqualTo(keyboardJson)
    }

    @Test
    fun fromJson() {
        val moshi = moshiBuilder.build()
        val adapter = moshi.adapter(Computer::class.java)
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor)
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse)
        assertThat(adapter.fromJson(keyboardJson)).isEqualTo(keyboard)

    }
}