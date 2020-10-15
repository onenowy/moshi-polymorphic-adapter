package dev.onenowy.moshipolymorphicadapter.maventests.codgen

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Assert.fail
import org.junit.Test

class CodegenDependencyTest {
    private val moshi = Moshi.Builder().build()
    private val computerAdapter = moshi.adapter(Computer::class.java)
    private val computerValueAdapter = moshi.adapter(ComputerValue::class.java)

    private val monitor = Monitor(1, "test")
    private val mouse = Mouse("mouse", "test")
    private val keyboard = Keyboard(true, "test")

    private val monitorJson = "{\"monitor_Unique\":1,\"testValue\":\"test\"}"
    private val mouseJson = "{\"mouseUnique\":\"mouse\",\"testValue\":\"test\"}"
    private val keyboardJson = "{\"keyboardUnique\":true,\"testValue\":\"test\"}"

    private val monitorValue = MonitorValue(1, "test")
    private val mouseValue = MouseValue("mouse", "test")
    private val keyboardValue = KeyboardValue(true, "test")

    private val monitorValueJson = "{\"type\":1,\"monitor\":1,\"testValue\":\"test\"}"
    private val mouseValueJson = "{\"type\":2,\"mouse\":\"mouse\",\"testValue\":\"test\"}"
    private val keyboardValueJson = "{\"type\":3,\"keyboard\":true,\"testValue\":\"test\"}"

    @Test
    fun toJson() {
        assertThat(computerAdapter.toJson(monitor)).isEqualTo(monitorJson)
        assertThat(computerAdapter.toJson(mouse)).isEqualTo(mouseJson)
        assertThat(computerAdapter.toJson(keyboard)).isEqualTo(keyboardJson)
        assertThat(computerValueAdapter.toJson(monitorValue)).isEqualTo(monitorValueJson)
        assertThat(computerValueAdapter.toJson(mouseValue)).isEqualTo(mouseValueJson)
        assertThat(computerValueAdapter.toJson(keyboardValue)).isEqualTo(keyboardValueJson)
    }

    @Test
    fun fromJson() {
        assertThat(computerAdapter.fromJson(monitorJson)).isEqualTo(monitor)
        assertThat(computerAdapter.fromJson(mouseJson)).isEqualTo(mouse)
        assertThat(computerAdapter.fromJson(keyboardJson)).isEqualTo(keyboard)
        assertThat(computerValueAdapter.fromJson(monitorValueJson)).isEqualTo(monitorValue)
        assertThat(computerValueAdapter.fromJson(mouseValueJson)).isEqualTo(mouseValue)
        assertThat(computerValueAdapter.fromJson(keyboardValueJson)).isEqualTo(keyboardValue)
    }

    @Test
    fun defaultValue() {
        assertThat(computerAdapter.fromJson(monitorValueJson)).isEqualTo(null)
        assertThat(computerAdapter.fromJson(mouseValueJson)).isEqualTo(null)
        assertThat(computerAdapter.fromJson(keyboardValueJson)).isEqualTo(null)
        try {
            computerValueAdapter.fromJson(monitorJson)
            fail()
        } catch (e: JsonDataException) {
            assertThat(e).hasMessageThat()
                .isEqualTo("Missing label for type")
        }
    }
}