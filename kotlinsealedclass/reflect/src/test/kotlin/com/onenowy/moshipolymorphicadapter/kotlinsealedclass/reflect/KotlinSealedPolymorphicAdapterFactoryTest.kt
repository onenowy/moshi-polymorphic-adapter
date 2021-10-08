package com.onenowy.moshipolymorphicadapter.kotlinsealedclass.reflect

import com.google.common.truth.Truth.assertThat
import com.onenowy.moshipolymorphicadapter.kotlinsealedclass.reflect.util.*
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Test

class KotlinSealedPolymorphicAdapterFactoryTest {
    val moshi = Moshi.Builder().add(KotlinSealedPolymorphicAdapterFactory()).build()
    val computerAdapter = moshi.adapter(Computer::class.java)
    val computerValueAdapter = moshi.adapter(ComputerValue::class.java)

    val monitor = Monitor(1)
    val mouse = Mouse("mouse")
    val keyboard = Keyboard(true)

    val monitorJson = "{\"monitorUnique\":1}"
    val mouseJson = "{\"mouseUnique\":\"mouse\"}"
    val keyboardJson = "{\"keyboardUnique\":true}"

    val monitorValue = MonitorValue(1)
    val mouseValue = MouseValue("mouse")
    val keyboardValue = KeyboardValue(true)

    val monitorValueJson = "{\"type\":1,\"monitor\":1}"
    val mouseValueJson = "{\"type\":2,\"mouse\":\"mouse\"}"
    val keyboardValueJson = "{\"type\":3,\"keyboard\":true}"

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
    fun FromJson() {
        assertThat(computerAdapter.fromJson(monitorJson)).isEqualTo(monitor)
        assertThat(computerAdapter.fromJson(mouseJson)).isEqualTo(mouse)
        assertThat(computerAdapter.fromJson(keyboardJson)).isEqualTo(keyboard)
        assertThat(computerValueAdapter.fromJson(monitorValueJson)).isEqualTo(monitorValue)
        assertThat(computerValueAdapter.fromJson(mouseValueJson)).isEqualTo(mouseValue)
        assertThat(computerValueAdapter.fromJson(keyboardValueJson)).isEqualTo(keyboardValue)
    }

    @Test
    fun unregisteredSubtype() {
        try {
            computerValueAdapter.fromJson(monitorJson)
        } catch (e: JsonDataException) {
            assertThat(e).hasMessageThat().isEqualTo("Missing label for type")
        }

        try {
            computerAdapter.fromJson(monitorValueJson)
        } catch (e: JsonDataException) {
            assertThat(e).hasMessageThat()
                .isEqualTo("No matching Field names for [monitorUnique, mouseUnique, keyboardUnique]")
        }
    }

    @Test
    fun defaultValue() {
        assertThat(computerAdapter.fromJson(monitorValueJson)).isEqualTo(null)
        assertThat(computerAdapter.fromJson(mouseValueJson)).isEqualTo(null)
        assertThat(computerAdapter.fromJson(keyboardValueJson)).isEqualTo(null)
        assertThat(computerValueAdapter.fromJson(monitorJson)).isEqualTo(null)
        assertThat(computerValueAdapter.fromJson(mouseJson)).isEqualTo(null)
        assertThat(computerValueAdapter.fromJson(keyboardJson)).isEqualTo(null)
    }
}