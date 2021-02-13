package com.onenowy.moshipolymorphicadapter

import com.google.common.truth.Truth.assertThat
import com.onenowy.moshipolymorphicadapter.util.Computer
import com.onenowy.moshipolymorphicadapter.util.Keyboard
import com.onenowy.moshipolymorphicadapter.util.Monitor
import com.onenowy.moshipolymorphicadapter.util.Mouse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Test

class ValueAdapterTest {
    val intFactory = ValueAdapterFactory.of(Computer::class.java, "typeInt", Int::class.javaObjectType)
        .withSubType(Monitor::class.java, Computer.ComTypeInt.Monitor.value)
        .withSubType(Keyboard::class.java, Computer.ComTypeInt.Keyboard.value).withSubType(Mouse::class.java, Computer.ComTypeInt.Mouse.value)

    val stringFacgtory = ValueAdapterFactory.of(Computer::class.java, "typeString", String::class.javaObjectType)
        .withSubType(Monitor::class.java, Computer.ComTypeString.Monitor.value)
        .withSubType(Keyboard::class.java, Computer.ComTypeString.Keyboard.value)
        .withSubType(Mouse::class.java, Computer.ComTypeString.Mouse.value)

    val doubleFactory = ValueAdapterFactory.of(Computer::class.java, "typeDouble", Double::class.javaObjectType)
        .withSubType(Monitor::class.java, Computer.ComTypeDouble.Monitor.value)
        .withSubType(Keyboard::class.java, Computer.ComTypeDouble.Keyboard.value)
        .withSubType(Mouse::class.java, Computer.ComTypeDouble.Mouse.value)

    val longFactory = ValueAdapterFactory.of(Computer::class.java, "typeLong", Long::class.javaObjectType)
        .withSubType(Monitor::class.java, Computer.ComTypeLong.Monitor.value)
        .withSubType(Keyboard::class.java, Computer.ComTypeLong.Keyboard.value).withSubType(Mouse::class.java, Computer.ComTypeLong.Mouse.value)


    private val monitor = Monitor(1)
    private val mouse = Mouse("mouse")
    private val keyboard = Keyboard(true)

    private val monitorJson =
        "{\"typeInt\":1,\"typeString\":\"1\",\"typeDouble\":5.0,\"typeLong\":${Long.MAX_VALUE - 2},\"monitorUnique\":1}"
    private val mouseJson = "{\"typeInt\":2,\"typeString\":\"2\",\"typeDouble\":10000.1,\"typeLong\":${Long.MAX_VALUE - 1}," +
            "\"mouseUnique\":\"mouse\"}"
    private val keyboardJson =
        "{\"typeInt\":3,\"typeString\":\"3\",\"typeDouble\":${Double.MAX_VALUE},\"typeLong\":${Long.MAX_VALUE},\"keyboardUnique\":true}"

    private fun getComputerAdapter(factory: JsonAdapter.Factory) = Moshi.Builder().add(factory).build().adapter(Computer::class.java)


    @Test
    fun toJson() {
        var adapter = getComputerAdapter(intFactory)
        assertThat(adapter.toJson(monitor)).contains("\"typeInt\":1")
        assertThat(adapter.toJson(mouse)).contains("\"typeInt\":2")
        assertThat(adapter.toJson(keyboard)).contains("\"typeInt\":3")
        adapter = getComputerAdapter(stringFacgtory)
        assertThat(adapter.toJson(monitor)).contains("\"typeString\":\"1\"")
        assertThat(adapter.toJson(mouse)).contains("\"typeString\":\"2\"")
        assertThat(adapter.toJson(keyboard)).contains("\"typeString\":\"3\"")
        adapter = getComputerAdapter(doubleFactory)
        assertThat(adapter.toJson(monitor)).contains("\"typeDouble\":5.0")
        assertThat(adapter.toJson(mouse)).contains("\"typeDouble\":10000.1")
        assertThat(adapter.toJson(keyboard)).contains("\"typeDouble\":${Double.MAX_VALUE}")
        adapter = getComputerAdapter(longFactory)
        assertThat(adapter.toJson(monitor)).contains("\"typeLong\":${Long.MAX_VALUE - 2}")
        assertThat(adapter.toJson(mouse)).contains("\"typeLong\":${Long.MAX_VALUE - 1}")
        assertThat(adapter.toJson(keyboard)).contains("\"typeLong\":${Long.MAX_VALUE}")
    }

    @Test
    fun fromJson() {
        var adapter = getComputerAdapter(intFactory)
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor)
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse)
        assertThat(adapter.fromJson(keyboardJson)).isEqualTo(keyboard)
        adapter = getComputerAdapter(stringFacgtory)
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor)
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse)
        assertThat(adapter.fromJson(keyboardJson)).isEqualTo(keyboard)
        adapter = getComputerAdapter(doubleFactory)
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor)
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse)
        assertThat(adapter.fromJson(keyboardJson)).isEqualTo(keyboard)
        adapter = getComputerAdapter(longFactory)
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor)
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse)
        assertThat(adapter.fromJson(keyboardJson)).isEqualTo(keyboard)
    }

    @Test
    fun unregisteredSubtype() {
        val propertyValueAdapterFactory = ValueAdapterFactory.of(Computer::class.java, "typeInt", Int::class.java)
        var adapter = getComputerAdapter(propertyValueAdapterFactory)

        try {
            adapter.toJson(monitor)
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat().isEqualTo("Expected one of [] but found $monitor, a ${monitor.javaClass}. Register this subtype.")
        }

        try {
            adapter.fromJson(monitorJson)
        } catch (e: JsonDataException) {
            assertThat(e).hasMessageThat().isEqualTo("Expected one of [] for key 'typeInt' but found 'null'. Register a subtype for this label.")
        }

        adapter = getComputerAdapter(propertyValueAdapterFactory.withSubType(Keyboard::class.java, Computer.ComTypeInt.Keyboard.value))
        try {
            adapter.toJson(monitor)
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat().isEqualTo(
                "Expected one of ${listOf(Keyboard::class.java)} but found $monitor, a ${monitor.javaClass}. Register " +
                        "this subtype."
            )
        }

        try {
            adapter.fromJson(monitorJson)
        } catch (e: JsonDataException) {
            assertThat(e).hasMessageThat().isEqualTo(
                "Expected one of ${listOf(Computer.ComTypeInt.Keyboard.value)} for key 'typeInt' but found '1'. " +
                        "Register a " +
                        "subtype for this label."
            )
        }
    }

    @Test
    fun unresigsterdLableKey() {
        val propertyValueAdapterFactory = ValueAdapterFactory.of(Computer::class.java, "wrongKey", Int::class.java)
            .withSubType(Monitor::class.java, Computer.ComTypeInt.Monitor.value)
            .withSubType(Keyboard::class.java, Computer.ComTypeInt.Keyboard.value).withSubType(Mouse::class.java, Computer.ComTypeInt.Mouse.value)
        val adapter = getComputerAdapter(propertyValueAdapterFactory)
        assertThat(adapter.toJson(monitor)).contains("\"wrongKey\":1")
        assertThat(adapter.toJson(mouse)).contains("\"wrongKey\":2")
        assertThat(adapter.toJson(keyboard)).contains("\"wrongKey\":3")
        try {
            adapter.fromJson(monitorJson)
        } catch (e: JsonDataException) {
            assertThat(e).hasMessageThat().isEqualTo("Missing label for wrongKey")
        }

    }

    @Test
    fun defaultValue() {
        val propertyValueAdapterFactory = ValueAdapterFactory.of(Computer::class.java, "typeInt", Int::class.java).withDefaultValue(monitor)
        val adapter = getComputerAdapter(propertyValueAdapterFactory)
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor)
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(monitor)
        assertThat(adapter.fromJson(keyboardJson)).isEqualTo(monitor)
        try {
            adapter.toJson(keyboard)
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat().isEqualTo("FallbackJsonAdapter with $monitor cannot make Json Object")
        }
    }
}