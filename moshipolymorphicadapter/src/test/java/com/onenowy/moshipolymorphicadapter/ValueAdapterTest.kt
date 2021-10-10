package com.onenowy.moshipolymorphicadapter

import com.google.common.truth.Truth.assertThat
import com.onenowy.moshipolymorphicadapter.util.Computer
import com.onenowy.moshipolymorphicadapter.util.Keyboard
import com.onenowy.moshipolymorphicadapter.util.Monitor
import com.onenowy.moshipolymorphicadapter.util.Mouse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Assert.fail
import org.junit.Test

class ValueAdapterTest {
    val intFactory = ValuePolymorphicAdapterFactory.of(Computer::class.java, "typeInt", Int::class.java)
        .withSubtype(Monitor::class.java, Computer.ComTypeInt.Monitor.value)
        .withSubtype(Keyboard::class.java, Computer.ComTypeInt.Keyboard.value)
        .withSubtype(Mouse::class.java, Computer.ComTypeInt.Mouse.value)


    val stringFacgtory = ValuePolymorphicAdapterFactory.of(Computer::class.java, "typeString", String::class.java)
        .withSubtype(Monitor::class.java, Computer.ComTypeString.Monitor.value)
        .withSubtype(Keyboard::class.java, Computer.ComTypeString.Keyboard.value)
        .withSubtype(Mouse::class.java, Computer.ComTypeString.Mouse.value)

    val doubleFactory = ValuePolymorphicAdapterFactory.of(Computer::class.java, "typeDouble", Double::class.java)
        .withSubtype(Monitor::class.java, Computer.ComTypeDouble.Monitor.value)
        .withSubtype(Keyboard::class.java, Computer.ComTypeDouble.Keyboard.value)
        .withSubtype(Mouse::class.java, Computer.ComTypeDouble.Mouse.value)

    val longFactory = ValuePolymorphicAdapterFactory.of(Computer::class.java, "typeLong", Long::class.java)
        .withSubtype(Monitor::class.java, Computer.ComTypeLong.Monitor.value)
        .withSubtype(Keyboard::class.java, Computer.ComTypeLong.Keyboard.value)
        .withSubtype(Mouse::class.java, Computer.ComTypeLong.Mouse.value)


    private val monitor = Monitor(1, "test")
    private val mouse = Mouse("mouse", "test")
    private val keyboard = Keyboard(true, "test")

    private val monitorJson =
        "{\"typeInt\":1,\"typeString\":\"1\",\"typeDouble\":5.0,\"typeLong\":${Long.MAX_VALUE - 2},\"monitorUnique\":1,\"testValue\":\"test\"}"
    private val mouseJson =
        "{\"typeInt\":2,\"typeString\":\"2\",\"typeDouble\":10000.1,\"typeLong\":${Long.MAX_VALUE - 1}," +
                "\"mouseUnique\":\"mouse\",\"testValue\":\"test\"}"
    private val keyboardJson =
        "{\"typeInt\":3,\"typeString\":\"3\",\"typeDouble\":${Double.MAX_VALUE},\"typeLong\":${Long.MAX_VALUE},\"keyboardUnique\":true,\"testValue\":\"test\"}"

    private fun getComputerAdapter(factory: JsonAdapter.Factory) =
        Moshi.Builder().add(factory).build().adapter(Computer::class.java)


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
        val propertyValueAdapterFactory =
            ValuePolymorphicAdapterFactory.of(Computer::class.java, "typeInt", Int::class.java)
        var adapter = getComputerAdapter(propertyValueAdapterFactory)

        try {
            adapter.toJson(monitor)
            fail()
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat()
                .isEqualTo("Expected one of [] but found $monitor, a ${monitor.javaClass}. Register this subtype.")
        }

        try {
            adapter.fromJson(monitorJson)
            fail()
        } catch (e: JsonDataException) {
            assertThat(e).hasMessageThat()
                .isEqualTo("Expected one of [] for key 'typeInt' but found '1'. Register a subtype for this label.")
        }

        adapter = getComputerAdapter(
            propertyValueAdapterFactory.withSubtype(
                Keyboard::class.java,
                Computer.ComTypeInt.Keyboard.value
            )
        )
        try {
            adapter.toJson(monitor)
            fail()
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat().isEqualTo(
                "Expected one of ${listOf(Keyboard::class.java)} but found $monitor, a ${monitor.javaClass}. Register " +
                        "this subtype."
            )
        }

        try {
            adapter.fromJson(monitorJson)
            fail()
        } catch (e: JsonDataException) {
            assertThat(e).hasMessageThat().isEqualTo(
                "Expected one of ${listOf(Computer.ComTypeInt.Keyboard.value)} for key 'typeInt' but found '1'. " +
                        "Register a " +
                        "subtype for this label."
            )
        }
    }

    @Test
    fun unregisteredLabelKey() {
        val propertyValueAdapterFactory =
            ValuePolymorphicAdapterFactory.of(Computer::class.java, "wrongKey", Int::class.java)
                .withSubtype(Monitor::class.java, Computer.ComTypeInt.Monitor.value)
                .withSubtype(Keyboard::class.java, Computer.ComTypeInt.Keyboard.value)
                .withSubtype(Mouse::class.java, Computer.ComTypeInt.Mouse.value)
        val adapter = getComputerAdapter(propertyValueAdapterFactory)
        assertThat(adapter.toJson(monitor)).contains("\"wrongKey\":1")
        assertThat(adapter.toJson(mouse)).contains("\"wrongKey\":2")
        assertThat(adapter.toJson(keyboard)).contains("\"wrongKey\":3")
        try {
            adapter.fromJson(monitorJson)
            fail()
        } catch (e: JsonDataException) {
            assertThat(e).hasMessageThat().isEqualTo("Missing label for wrongKey")
        }

    }

    @Test
    fun defaultValue() {
        val propertyValueAdapterFactory =
            ValuePolymorphicAdapterFactory.of(Computer::class.java, "typeInt", Int::class.java)
                .withDefaultValue(monitor)
        val adapter = getComputerAdapter(propertyValueAdapterFactory)
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor)
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(monitor)
        assertThat(adapter.fromJson(keyboardJson)).isEqualTo(monitor)
        try {
            adapter.toJson(keyboard)
            fail()
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat().isEqualTo("FallbackJsonAdapter with $monitor cannot make Json Object")
        }
    }

    @Test
    fun notUniqueSubtype() {
        val notUnique = ValuePolymorphicAdapterFactory.of(Computer::class.java, "typeInt", Int::class.java)
            .withSubtype(Monitor::class.java, Computer.ComTypeInt.Monitor.value)
            .withSubtype(Monitor::class.java, Computer.ComTypeInt.Keyboard.value)
            .withSubtype(Mouse::class.java, Computer.ComTypeInt.Mouse.value)
        val adapter = getComputerAdapter(notUnique)
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor)
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse)
        assertThat(adapter.fromJson("{\"typeInt\":3,\"monitorUnique\":1,\"testValue\":\"test\"}")).isEqualTo(monitor)
        assertThat(
            adapter.toJson(
                Monitor(
                    1,
                    "test"
                )
            )
        ).isEqualTo("{\"typeInt\":1,\"monitorUnique\":1,\"testValue\":\"test\"}")
    }

    @Test
    fun uniqueLabel() {
        try {
            ValuePolymorphicAdapterFactory.of(Computer::class.java, "typeInt", Int::class.java)
                .withSubtype(Monitor::class.java, Computer.ComTypeInt.Monitor.value)
                .withSubtype(Keyboard::class.java, Computer.ComTypeInt.Monitor.value)
                .withSubtype(Mouse::class.java, Computer.ComTypeInt.Mouse.value)
            fail()
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat().isEqualTo("${Computer.ComTypeInt.Monitor.value} must be unique")
        }

        try {
            val subtypes = listOf(Monitor::class.java, Keyboard::class.java, Mouse::class.java)
            val labels = listOf(
                Computer.ComTypeInt.Monitor.value,
                Computer.ComTypeInt.Monitor.value,
                Computer.ComTypeInt.Monitor.value
            )
            ValuePolymorphicAdapterFactory.of(Computer::class.java, "typeInt", Int::class.java)
                .withSubtypes(subtypes, labels)
            fail()
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat().isEqualTo("The value for ${Computer::class.java.simpleName} must be unique")
        }
    }

    @Test
    fun notEqualNumberWithSubtypes() {
        try {
            val subtypes = listOf(Monitor::class.java, Keyboard::class.java, Mouse::class.java)
            val labels = listOf(Computer.ComTypeInt.Monitor.value)
            ValuePolymorphicAdapterFactory.of(Computer::class.java, "typeInt", Int::class.java)
                .withSubtypes(subtypes, labels)
            fail()
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat()
                .isEqualTo("The number of values for ${Computer::class.java.simpleName} is different from subtypes")
        }
    }
}