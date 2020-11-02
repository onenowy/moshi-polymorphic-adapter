package com.onenowy.moshipolymorphicadapter

import com.google.common.truth.Truth
import com.onenowy.moshipolymorphicadapter.util.Computer
import com.onenowy.moshipolymorphicadapter.util.Keyboard
import com.onenowy.moshipolymorphicadapter.util.Monitor
import com.onenowy.moshipolymorphicadapter.util.Mouse
import com.squareup.moshi.Moshi
import org.junit.Test

class PropertyValueAdapterTest {
    private val moshiInt = Moshi.Builder().add(
        PropertyValueAdapterFactory.of(Computer::class.java, "typeInt", Int::class.java)
            .withSubType(Monitor::class.java, Computer.ComTypeInt.Monitor.value)
            .withSubType(Keyboard::class.java, Computer.ComTypeInt.Keyboard.value).withSubType(Mouse::class.java, Computer.ComTypeInt.Mouse.value)
    ).build()
    private val moshiString = Moshi.Builder().add(
        PropertyValueAdapterFactory.of(Computer::class.java, "typeString", String::class.java)
            .withSubType(Monitor::class.java, Computer.ComTypeString.Monitor.value)
            .withSubType(Keyboard::class.java, Computer.ComTypeString.Keyboard.value)
            .withSubType(Mouse::class.java, Computer.ComTypeString.Mouse.value)
    ).build()

    private val moshiDouble = Moshi.Builder().add(
        PropertyValueAdapterFactory.of(Computer::class.java, "typeDouble", Double::class.java)
            .withSubType(Monitor::class.java, Computer.ComTypeDouble.Monitor.value)
            .withSubType(Keyboard::class.java, Computer.ComTypeDouble.Keyboard.value)
            .withSubType(Mouse::class.java, Computer.ComTypeDouble.Mouse.value)
    ).build()

    private val moshiLong = Moshi.Builder().add(
        PropertyValueAdapterFactory.of(Computer::class.java, "typeLong", Long::class.java)
            .withSubType(Monitor::class.java, Computer.ComTypeLong.Monitor.value)
            .withSubType(Keyboard::class.java, Computer.ComTypeLong.Keyboard.value).withSubType(Mouse::class.java, Computer.ComTypeLong.Mouse.value)
    ).build()


    private val monitor = Monitor(1)
    private val mouse = Mouse("mouse")
    private val keyboard = Keyboard(true)

    private val monitorJson =
        "{\"typeInt\":1,\"typeString\":\"1\",\"typeDouble\":1.0,\"typeLong\":${Long.MAX_VALUE - 2},\"monitorUnique\":1}"
    private val mouseJson = "{\"typeInt\":2,\"typeString\":\"2\",\"typeDouble\":10000.1,\"typeLong\":${Long.MAX_VALUE - 1}," +
            "\"mouseUnique\":\"mouse\"}"
    private val keyboardJson =
        "{\"typeInt\":3,\"typeString\":\"3\",\"typeDouble\":${Double.MAX_VALUE},\"typeLong\":${Long.MAX_VALUE},\"keyboardUnique\":true}"

    @Test
    fun toJson() {
        var adapter = moshiInt.adapter(Computer::class.java)
        Truth.assertThat(adapter.toJson(monitor)).contains("\"typeInt\":1")
        Truth.assertThat(adapter.toJson(mouse)).contains("\"typeInt\":2")
        Truth.assertThat(adapter.toJson(keyboard)).contains("\"typeInt\":3")
        adapter = moshiString.adapter(Computer::class.java)
        Truth.assertThat(adapter.toJson(monitor)).contains("\"typeString\":\"1\"")
        Truth.assertThat(adapter.toJson(mouse)).contains("\"typeString\":\"2\"")
        Truth.assertThat(adapter.toJson(keyboard)).contains("\"typeString\":\"3\"")
        adapter = moshiDouble.adapter(Computer::class.java)
        Truth.assertThat(adapter.toJson(monitor)).contains("\"typeDouble\":1.0")
        Truth.assertThat(adapter.toJson(mouse)).contains("\"typeDouble\":10000.1")
        Truth.assertThat(adapter.toJson(keyboard)).contains("\"typeDouble\":${Double.MAX_VALUE}")
        adapter = moshiLong.adapter(Computer::class.java)
        Truth.assertThat(adapter.toJson(monitor)).contains("\"typeLong\":${Long.MAX_VALUE - 2}")
        Truth.assertThat(adapter.toJson(mouse)).contains("\"typeLong\":${Long.MAX_VALUE - 1}")
        Truth.assertThat(adapter.toJson(keyboard)).contains("\"typeLong\":${Long.MAX_VALUE}")
    }

    @Test
    fun fromJson() {
        var adapter = moshiInt.adapter(Computer::class.java)
        Truth.assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor)
        Truth.assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse)
        Truth.assertThat(adapter.fromJson(keyboardJson)).isEqualTo(keyboard)
        adapter = moshiString.adapter(Computer::class.java)
        Truth.assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor)
        Truth.assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse)
        Truth.assertThat(adapter.fromJson(keyboardJson)).isEqualTo(keyboard)
        adapter = moshiDouble.adapter(Computer::class.java)
        Truth.assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor)
        Truth.assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse)
        Truth.assertThat(adapter.fromJson(keyboardJson)).isEqualTo(keyboard)
        adapter = moshiLong.adapter(Computer::class.java)
        Truth.assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor)
        Truth.assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse)
        Truth.assertThat(adapter.fromJson(keyboardJson)).isEqualTo(keyboard)
    }
}