package com.onenowy.moshipolymorphicadapter.kotlinsealed.reflect

import com.google.common.truth.Truth.assertThat
import com.onenowy.moshipolymorphicadapter.kotlinsealed.reflect.util.*
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Assert.fail
import org.junit.Test

class KotlinSealedPolymorphicAdapterFactoryTest {
    val moshi = Moshi.Builder().add(KotlinSealedPolymorphicAdapterFactory()).build()
    val computerAdapter = moshi.adapter(Computer::class.java)
    val computerValueAdapter = moshi.adapter(ComputerValue::class.java)

    val monitor = Monitor(1, "test")
    val mouse = Mouse("mouse", "test")
    val keyboard = Keyboard(true, "test")

    val monitorJson = "{\"monitorUnique\":1,\"testValue\":\"test\"}"
    val mouseJson = "{\"mouseUnique\":\"mouse\",\"testValue\":\"test\"}"
    val keyboardJson = "{\"keyboardUnique\":true,\"testValue\":\"test\"}"

    val monitorValue = MonitorValue(1, "test")
    val mouseValue = MouseValue("mouse", "test")
    val keyboardValue = KeyboardValue(true, "test")

    val monitorValueJson = "{\"type\":1,\"monitor\":1,\"testValue\":\"test\"}"
    val mouseValueJson = "{\"type\":2,\"mouse\":\"mouse\",\"testValue\":\"test\"}"
    val keyboardValueJson = "{\"type\":3,\"keyboard\":true,\"testValue\":\"test\"}"

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
    fun romJson() {
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

    @Test
    fun notSealed() {
        try {
            moshi.adapter(NotSealedComputerValue::class.java)
            fail()
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat()
                .isEqualTo("${NotSealedComputerValue::class.simpleName} is not a sealed class")
        }
    }
}