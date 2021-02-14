package com.onenowy.moshipolymorphicadapter

import com.google.common.truth.Truth.assertThat
import com.onenowy.moshipolymorphicadapter.moshipolymorphicadapterfactory.NameAdapterFactory
import com.onenowy.moshipolymorphicadapter.util.Computer
import com.onenowy.moshipolymorphicadapter.util.Keyboard
import com.onenowy.moshipolymorphicadapter.util.Monitor
import com.onenowy.moshipolymorphicadapter.util.Mouse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Test

class NameAdapterTest {

    val nameAdapterFactory = NameAdapterFactory.of(Computer::class.java)
    val withSubtype = nameAdapterFactory.withSubtype(
        Monitor::class.java,
        "monitorUnique"
    ).withSubtype(Mouse::class.java, "mouseUnique").withSubtype(Keyboard::class.java, "keyboardUnique")
    val withSubtypes = nameAdapterFactory.withSubTypes(
        listOf(Monitor::class.java, Mouse::class.java, Keyboard::class.java),
        listOf("monitorUnique", "mouseUnique", "keyboardUnique")
    )


    val monitor = Monitor(1)
    val mouse = Mouse("mouse")
    val keyboard = Keyboard(true)

    val monitorJson = "{\"monitorUnique\":1}"
    val mouseJson = "{\"mouseUnique\":\"mouse\"}"
    val keyboardJson = "{\"keyboardUnique\":true}"

    fun getComputerAdapter(factory: JsonAdapter.Factory) = Moshi.Builder().add(factory).build().adapter(Computer::class.java)

    @Test
    fun toJson() {
        var adapter = getComputerAdapter(withSubtype)
        assertThat(adapter.toJson(monitor)).isEqualTo(monitorJson)
        assertThat(adapter.toJson(mouse)).isEqualTo(mouseJson)
        assertThat(adapter.toJson(keyboard)).isEqualTo(keyboardJson)
        adapter = getComputerAdapter(withSubtypes)
        assertThat(adapter.toJson(monitor)).isEqualTo(monitorJson)
        assertThat(adapter.toJson(mouse)).isEqualTo(mouseJson)
        assertThat(adapter.toJson(keyboard)).isEqualTo(keyboardJson)
    }

    @Test
    fun fromJson() {
        var adapter = getComputerAdapter(withSubtype)
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor)
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse)
        assertThat(adapter.fromJson(keyboardJson)).isEqualTo(keyboard)
        adapter = getComputerAdapter(withSubtypes)
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor)
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse)
        assertThat(adapter.fromJson(keyboardJson)).isEqualTo(keyboard)
    }

    @Test
    fun unregisteredSubtype() {
        var adapter = getComputerAdapter(nameAdapterFactory)
        try {
            adapter.toJson(monitor)
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat().isEqualTo("Expected one of [] but found $monitor, a ${monitor.javaClass}. Register this subtype.")
        }

        try {
            adapter.fromJson(monitorJson)
        } catch (e: JsonDataException) {
            assertThat(e).hasMessageThat().isEqualTo("No matching Field names for []")
        }
        adapter = getComputerAdapter(nameAdapterFactory.withSubtype(Monitor::class.java, "test"))

        assertThat(adapter.toJson(monitor)).isEqualTo(monitorJson)

        try {
            adapter.fromJson(monitorJson)
        } catch (e: JsonDataException) {
            println(e)
            assertThat(e).hasMessageThat().isEqualTo("No matching Field names for [test]")
        }
    }

    @Test
    fun defaultValue() {
        val adapter = getComputerAdapter(nameAdapterFactory.withDefaultValue(monitor))
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor)
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(monitor)
        assertThat(adapter.fromJson(keyboardJson)).isEqualTo(monitor)
        try {
            adapter.toJson(monitor)
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat().isEqualTo("FallbackJsonAdapter with $monitor cannot make Json Object")
        }
    }
}