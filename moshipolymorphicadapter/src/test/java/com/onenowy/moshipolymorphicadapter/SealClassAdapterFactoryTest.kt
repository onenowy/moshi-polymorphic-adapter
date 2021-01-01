package com.onenowy.moshipolymorphicadapter

import com.google.common.truth.Truth
import com.onenowy.moshipolymorphicadapter.util.*
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import org.junit.Test

class SealClassAdapterFactoryTest {
    val computerAdapter = Moshi.Builder().add(SealedClassFactorySelector(Computer::class).getAdapterFactory()).build().adapter(Computer::class.java)
    val computerValueAdapter = Moshi.Builder().add(SealedClassFactorySelector(ComputerValue::class).getAdapterFactory()).build().adapter(ComputerValue::class.java)

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
        Truth.assertThat(computerAdapter.toJson(monitor)).isEqualTo(monitorJson)
        Truth.assertThat(computerAdapter.toJson(mouse)).isEqualTo(mouseJson)
        Truth.assertThat(computerAdapter.toJson(keyboard)).isEqualTo(keyboardJson)
        Truth.assertThat(computerValueAdapter.toJson(monitorValue)).isEqualTo(monitorValueJson)
        Truth.assertThat(computerValueAdapter.toJson(mouseValue)).isEqualTo(mouseValueJson)
        Truth.assertThat(computerValueAdapter.toJson(keyboardValue)).isEqualTo(keyboardValueJson)
    }

    @Test
    fun FromJson() {
        Truth.assertThat(computerAdapter.fromJson(monitorJson)).isEqualTo(monitor)
        Truth.assertThat(computerAdapter.fromJson(mouseJson)).isEqualTo(mouse)
        Truth.assertThat(computerAdapter.fromJson(keyboardJson)).isEqualTo(keyboard)
        Truth.assertThat(computerValueAdapter.fromJson(monitorValueJson)).isEqualTo(monitorValue)
        Truth.assertThat(computerValueAdapter.fromJson(mouseValueJson)).isEqualTo(mouseValue)
        Truth.assertThat(computerValueAdapter.fromJson(keyboardValueJson)).isEqualTo(keyboardValue)
    }

    @Test
    fun unregisteredSubtype() {
        try {
            computerValueAdapter.fromJson(monitorJson)
        } catch (e: JsonDataException) {
            Truth.assertThat(e).hasMessageThat().isEqualTo("Missing label for type")
        }

        try {
            computerAdapter.fromJson(monitorValueJson)
        } catch (e: JsonDataException) {
            Truth.assertThat(e).hasMessageThat().isEqualTo("No matching property names for [monitorUnique, mouseUnique, keyboardUnique]")
        }
    }

    @Test
    fun defaultValue() {
        val computerDefaultAdapter =
            Moshi.Builder().add(SealedClassFactorySelector(Computer::class).getAdapterFactory().withDefaultValue(monitor)).build().adapter(Computer::class.java)
        val computerValueDefaultAdapter =
            Moshi.Builder().add(SealedClassFactorySelector(ComputerValue::class).getAdapterFactory().withDefaultValue(monitorValue)).build()
                .adapter(ComputerValue::class.java)

        Truth.assertThat(computerDefaultAdapter.fromJson(monitorValueJson)).isEqualTo(monitor)
        Truth.assertThat(computerDefaultAdapter.fromJson(mouseValueJson)).isEqualTo(monitor)
        Truth.assertThat(computerDefaultAdapter.fromJson(keyboardValueJson)).isEqualTo(monitor)
        Truth.assertThat(computerValueDefaultAdapter.fromJson(monitorJson)).isEqualTo(monitorValue)
        Truth.assertThat(computerValueDefaultAdapter.fromJson(mouseJson)).isEqualTo(monitorValue)
        Truth.assertThat(computerValueDefaultAdapter.fromJson(keyboardJson)).isEqualTo(monitorValue)
    }
}