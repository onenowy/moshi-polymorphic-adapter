package dev.onenowy.moshipolymorphicadapter

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Assert.fail
import org.junit.Test

class NamePolymorphicAdapterTest {

    private val nameAdapterFactory = NamePolymorphicAdapterFactory.of(Computer::class.java)
    private val withSubtype = nameAdapterFactory.withSubtype(
        Monitor::class.java,
        "monitorUnique"
    ).withSubtype(Mouse::class.java, "mouseUnique").withSubtype(Keyboard::class.java, "keyboardUnique")
    private val withSubtypes = nameAdapterFactory.withSubtypes(
        listOf(Monitor::class.java, Mouse::class.java, Keyboard::class.java),
        listOf("monitorUnique", "mouseUnique", "keyboardUnique")
    )
    private val monitor = Monitor(1, "test")
    private val mouse = Mouse("mouse", "test")
    private val keyboard = Keyboard(true, "test")

    private val monitorJson = "{\"monitorUnique\":1,\"testValue\":\"test\"}"
    private val mouseJson = "{\"mouseUnique\":\"mouse\",\"testValue\":\"test\"}"
    private val keyboardJson = "{\"keyboardUnique\":true,\"testValue\":\"test\"}"

    private fun getComputerAdapter(factory: JsonAdapter.Factory) =
        Moshi.Builder().add(factory).build().adapter(Computer::class.java)

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
            fail()
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat()
                .isEqualTo("Expected one of [] but found $monitor, a ${monitor.javaClass}. Register this subtype.")
        }

        try {
            adapter.fromJson(monitorJson)
            fail()
        } catch (e: JsonDataException) {
            assertThat(e).hasMessageThat().isEqualTo("No matching Field names for []")
        }
        adapter = getComputerAdapter(nameAdapterFactory.withSubtype(Monitor::class.java, "test"))

        assertThat(adapter.toJson(monitor)).isEqualTo(monitorJson)

        try {
            adapter.fromJson(monitorJson)
            fail()
        } catch (e: JsonDataException) {
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
            fail()
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat().isEqualTo("FallbackJsonAdapter with $monitor cannot make Json Object")
        }
    }

    @Test
    fun notUniqueSubtype() {
        val notUniqueWithSubtype = nameAdapterFactory.withSubtype(
            Monitor::class.java,
            "monitorUnique"
        ).withSubtype(Mouse::class.java, "mouseUnique").withSubtype(Monitor::class.java, "keyboardUnique")
        val adapter = getComputerAdapter(notUniqueWithSubtype)
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor)
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse)
        assertThat(adapter.fromJson("{\"keyboardUnique\":1,\"testValue\":\"test\"}")).isEqualTo(Monitor(null, "test"))
        assertThat(adapter.toJson(Monitor(1, "test"))).isEqualTo("{\"monitorUnique\":1,\"testValue\":\"test\"}")
    }

    @Test
    fun uniqueLabel() {
        try {
            nameAdapterFactory.withSubtype(
                Monitor::class.java,
                "monitorUnique"
            ).withSubtype(Mouse::class.java, "mouseUnique").withSubtype(Keyboard::class.java, "monitorUnique")
            fail()
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat().isEqualTo("monitorUnique must be unique")
        }

        try {
            nameAdapterFactory.withSubtypes(
                listOf(Monitor::class.java, Mouse::class.java, Keyboard::class.java),
                listOf("monitorUnique", "mouseUnique", "monitorUnique")
            )
            fail()
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat()
                .isEqualTo("The label name for ${Computer::class.java.simpleName} must be unique")
        }
    }

    @Test
    fun notEqualNumberWithSubtypes() {
        try {
            nameAdapterFactory.withSubtypes(
                listOf(Monitor::class.java, Mouse::class.java, Keyboard::class.java),
                listOf("monitorUnique", "mouseUnique")
            )
            fail()
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat()
                .isEqualTo("The number of label names for ${Computer::class.java.simpleName} is different from subtypes")
        }
    }

    @Test
    fun javaInterfaceWithCustomName() {
        val adapter = Moshi.Builder().add(
            NamePolymorphicAdapterFactory.of(ComputerJava::class.java)
                .withSubtypes(
                    listOf(
                        MouseJava::class.java,
                        KeyboardJava::class.java,
                        MonitorJava::class.java
                    ),
                    listOf("unique_mouse", "uniqueKeyboard", "uniqueMonitor")
                )
        ).build()
            .adapter(ComputerJava::class.java)
        val monitorJava = MonitorJava("test")
        val mouseJava = MouseJava(Long.MAX_VALUE)
        val keyboardJava = KeyboardJava(true)
        val monitorJavaJson = "{\"uniqueMonitor\":\"test\"}"
        val mouseJavaJson = "{\"unique_mouse\":" + Long.MAX_VALUE + "}"
        val keyboardJavaJson = "{\"uniqueKeyboard\":true}"
        assertThat(adapter.toJson(monitorJava)).isEqualTo(monitorJavaJson)
        assertThat(adapter.toJson(mouseJava)).isEqualTo(mouseJavaJson)
        assertThat(adapter.toJson(keyboardJava)).isEqualTo(keyboardJavaJson)
        assertThat(adapter.fromJson(monitorJavaJson)).isEqualTo(monitorJava)
        assertThat(adapter.fromJson(mouseJavaJson)).isEqualTo(mouseJava)
        assertThat(adapter.fromJson(keyboardJavaJson)).isEqualTo(keyboardJava)
    }
}