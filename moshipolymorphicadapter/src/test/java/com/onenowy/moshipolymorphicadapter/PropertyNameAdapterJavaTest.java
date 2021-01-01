package com.onenowy.moshipolymorphicadapter;

import com.google.common.truth.Truth;
import com.onenowy.moshipolymorphicadapter.util.Computer;
import com.onenowy.moshipolymorphicadapter.util.Keyboard;
import com.onenowy.moshipolymorphicadapter.util.Monitor;
import com.onenowy.moshipolymorphicadapter.util.Mouse;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;

import kotlin.collections.CollectionsKt;


public class PropertyNameAdapterJavaTest {
    @NotNull
    private final NameAdapterFactory nameAdapterFactory;
    @NotNull
    private final NameAdapterFactory withSubtype;
    @NotNull
    private final NameAdapterFactory withSubtypes;
    @NotNull
    private final Monitor monitor;
    @NotNull
    private final Mouse mouse;
    @NotNull
    private final Keyboard keyboard;
    @NotNull
    private final String monitorJson;
    @NotNull
    private final String mouseJson;
    @NotNull
    private final String keyboardJson;

    public PropertyNameAdapterJavaTest() {
        this.nameAdapterFactory = NameAdapterFactory.Companion.of(Computer.class);
        this.withSubtype = this.nameAdapterFactory.withSubtype(Monitor.class, "monitorUnique").withSubtype(Mouse.class, "mouseUnique").withSubtype(Keyboard.class, "keyboardUnique");
        this.withSubtypes = this.nameAdapterFactory.withSubTypes(CollectionsKt.listOf(Monitor.class, Mouse.class, Keyboard.class), CollectionsKt.listOf("monitorUnique", "mouseUnique", "keyboardUnique"));
        this.monitor = new Monitor(1);
        this.mouse = new Mouse("mouse");
        this.keyboard = new Keyboard(true);
        this.monitorJson = "{\"monitorUnique\":1}";
        this.mouseJson = "{\"mouseUnique\":\"mouse\"}";
        this.keyboardJson = "{\"keyboardUnique\":true}";
    }

    @NotNull
    public final NameAdapterFactory getNameAdapterFactory() {
        return this.nameAdapterFactory;
    }

    @NotNull
    public final NameAdapterFactory getWithSubtype() {
        return this.withSubtype;
    }

    @NotNull
    public final NameAdapterFactory getWithSubtypes() {
        return this.withSubtypes;
    }

    @NotNull
    public final Monitor getMonitor() {
        return this.monitor;
    }

    @NotNull
    public final Mouse getMouse() {
        return this.mouse;
    }

    @NotNull
    public final Keyboard getKeyboard() {
        return this.keyboard;
    }

    @NotNull
    public final String getMonitorJson() {
        return this.monitorJson;
    }

    @NotNull
    public final String getMouseJson() {
        return this.mouseJson;
    }

    @NotNull
    public final String getKeyboardJson() {
        return this.keyboardJson;
    }

    private final JsonAdapter getComputerAdapter(JsonAdapter.Factory factory) {
        return (new Moshi.Builder()).add(factory).build().adapter(Computer.class);
    }

    @Test
    public final void toJson() {
        JsonAdapter adapter = this.getComputerAdapter(this.withSubtype);
        Truth.assertThat(adapter.toJson(this.monitor)).isEqualTo(this.monitorJson);
        Truth.assertThat(adapter.toJson(this.mouse)).isEqualTo(this.mouseJson);
        Truth.assertThat(adapter.toJson(this.keyboard)).isEqualTo(this.keyboardJson);
        adapter = this.getComputerAdapter(this.withSubtypes);
        Truth.assertThat(adapter.toJson(this.monitor)).isEqualTo(this.monitorJson);
        Truth.assertThat(adapter.toJson(this.mouse)).isEqualTo(this.mouseJson);
        Truth.assertThat(adapter.toJson(this.keyboard)).isEqualTo(this.keyboardJson);
    }

    @Test
    public final void fromJson() throws IOException {
        JsonAdapter adapter = this.getComputerAdapter(this.withSubtype);
        Truth.assertThat(adapter.fromJson(this.monitorJson)).isEqualTo(this.monitor);
        Truth.assertThat(adapter.fromJson(this.mouseJson)).isEqualTo(this.mouse);
        Truth.assertThat(adapter.fromJson(this.keyboardJson)).isEqualTo(this.keyboard);
        adapter = this.getComputerAdapter(this.withSubtypes);
        Truth.assertThat(adapter.fromJson(this.monitorJson)).isEqualTo(this.monitor);
        Truth.assertThat(adapter.fromJson(this.mouseJson)).isEqualTo(this.mouse);
        Truth.assertThat(adapter.fromJson(this.keyboardJson)).isEqualTo(this.keyboard);
    }

    @Test
    public final void unregisteredSubtype() throws IOException {
        JsonAdapter adapter = this.getComputerAdapter(this.nameAdapterFactory);

        try {
            adapter.toJson(this.monitor);
        } catch (IllegalArgumentException var6) {
            System.out.println(var6);
            Truth.assertThat(var6).hasMessageThat().isEqualTo("Expected one of [] but found " + this.monitor + ", a " + this.monitor.getClass() + ". Register this subtype.");
        }

        try {
            adapter.fromJson(this.monitorJson);
        } catch (JsonDataException var5) {
            System.out.println(var5);
            Truth.assertThat(var5).hasMessageThat().isEqualTo("No matching property names for []");
        }

        adapter = this.getComputerAdapter(this.nameAdapterFactory.withSubtype(Monitor.class, "test"));
        Truth.assertThat(adapter.toJson(this.monitor)).isEqualTo(this.monitorJson);

        try {
            adapter.fromJson(this.monitorJson);
        } catch (JsonDataException var4) {
            boolean var3 = false;
            System.out.println(var4);
            Truth.assertThat(var4).hasMessageThat().isEqualTo("No matching property names for " +
                    "[test]");
        }

    }

    @Test
    public final void defaultValue() throws IOException {
        JsonAdapter adapter =
                this.getComputerAdapter(this.nameAdapterFactory.withDefaultValue(this.monitor));
        Truth.assertThat(adapter.fromJson(this.monitorJson)).isEqualTo(this.monitor);
        Truth.assertThat(adapter.fromJson(this.mouseJson)).isEqualTo(this.monitor);
        Truth.assertThat(adapter.fromJson(this.keyboardJson)).isEqualTo(this.monitor);

        try {
            adapter.toJson(this.monitor);
        } catch (IllegalArgumentException var3) {
            System.out.println(var3);
            Truth.assertThat(var3).hasMessageThat().isEqualTo("FallbackJsonAdapter with "+monitor+" cannot make Json Object");
        }
    }
}
