package com.onenowy.moshipolymorphicadapter;

import com.onenowy.moshipolymorphicadapter.util.Computer;
import com.onenowy.moshipolymorphicadapter.util.Keyboard;
import com.onenowy.moshipolymorphicadapter.util.Monitor;
import com.onenowy.moshipolymorphicadapter.util.Mouse;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import kotlin.collections.CollectionsKt;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;


public class NameAdapterJavaTest {
    @NotNull
    private final NamePolymorphicAdapterFactory nameAdapterFactory;
    @NotNull
    private final NamePolymorphicAdapterFactory withSubtype;
    @NotNull
    private final NamePolymorphicAdapterFactory withSubtypes;
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

    public NameAdapterJavaTest() {
        this.nameAdapterFactory = NamePolymorphicAdapterFactory.Companion.of(Computer.class);
        this.withSubtype = this.nameAdapterFactory.withSubtype(Monitor.class, "monitorUnique").withSubtype(Mouse.class, "mouseUnique").withSubtype(Keyboard.class, "keyboardUnique");
        this.withSubtypes = this.nameAdapterFactory.withSubtypes(CollectionsKt.listOf(Monitor.class, Mouse.class, Keyboard.class), CollectionsKt.listOf("monitorUnique", "mouseUnique", "keyboardUnique"));
        this.monitor = new Monitor(1, "test");
        this.mouse = new Mouse("mouse", "test");
        this.keyboard = new Keyboard(true, "test");
        this.monitorJson = "{\"monitorUnique\":1,\"testValue\":\"test\"}";
        this.mouseJson = "{\"mouseUnique\":\"mouse\",\"testValue\":\"test\"}";
        this.keyboardJson = "{\"keyboardUnique\":true,\"testValue\":\"test\"}";
    }

    @NotNull
    public final NamePolymorphicAdapterFactory getNameAdapterFactory() {
        return this.nameAdapterFactory;
    }

    @NotNull
    public final NamePolymorphicAdapterFactory getWithSubtype() {
        return this.withSubtype;
    }

    @NotNull
    public final NamePolymorphicAdapterFactory getWithSubtypes() {
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
        assertThat(adapter.toJson(this.monitor)).isEqualTo(this.monitorJson);
        assertThat(adapter.toJson(this.mouse)).isEqualTo(this.mouseJson);
        assertThat(adapter.toJson(this.keyboard)).isEqualTo(this.keyboardJson);
        adapter = this.getComputerAdapter(this.withSubtypes);
        assertThat(adapter.toJson(this.monitor)).isEqualTo(this.monitorJson);
        assertThat(adapter.toJson(this.mouse)).isEqualTo(this.mouseJson);
        assertThat(adapter.toJson(this.keyboard)).isEqualTo(this.keyboardJson);
    }

    @Test
    public final void fromJson() throws IOException {
        JsonAdapter adapter = this.getComputerAdapter(this.withSubtype);
        assertThat(adapter.fromJson(this.monitorJson)).isEqualTo(this.monitor);
        assertThat(adapter.fromJson(this.mouseJson)).isEqualTo(this.mouse);
        assertThat(adapter.fromJson(this.keyboardJson)).isEqualTo(this.keyboard);
        adapter = this.getComputerAdapter(this.withSubtypes);
        assertThat(adapter.fromJson(this.monitorJson)).isEqualTo(this.monitor);
        assertThat(adapter.fromJson(this.mouseJson)).isEqualTo(this.mouse);
        assertThat(adapter.fromJson(this.keyboardJson)).isEqualTo(this.keyboard);
    }

    @Test
    public final void unregisteredSubtype() throws IOException {
        JsonAdapter adapter = this.getComputerAdapter(this.nameAdapterFactory);

        try {
            adapter.toJson(this.monitor);
        } catch (IllegalArgumentException var6) {
            System.out.println(var6);
            assertThat(var6).hasMessageThat().isEqualTo("Expected one of [] but found " + this.monitor + ", a " + this.monitor.getClass() + ". Register this subtype.");
        }

        try {
            adapter.fromJson(this.monitorJson);
        } catch (JsonDataException var5) {
            System.out.println(var5);
            assertThat(var5).hasMessageThat().isEqualTo("No matching Field names for []");
        }

        adapter = this.getComputerAdapter(this.nameAdapterFactory.withSubtype(Monitor.class, "test"));
        assertThat(adapter.toJson(this.monitor)).isEqualTo(this.monitorJson);

        try {
            adapter.fromJson(this.monitorJson);
        } catch (JsonDataException var4) {
            boolean var3 = false;
            System.out.println(var4);
            assertThat(var4).hasMessageThat().isEqualTo("No matching Field names for " +
                    "[test]");
        }

    }

    @Test
    public final void defaultValue() throws IOException {
        JsonAdapter adapter =
                this.getComputerAdapter(this.nameAdapterFactory.withDefaultValue(this.monitor));
        assertThat(adapter.fromJson(this.monitorJson)).isEqualTo(this.monitor);
        assertThat(adapter.fromJson(this.mouseJson)).isEqualTo(this.monitor);
        assertThat(adapter.fromJson(this.keyboardJson)).isEqualTo(this.monitor);

        try {
            adapter.toJson(this.monitor);
        } catch (IllegalArgumentException var3) {
            System.out.println(var3);
            assertThat(var3).hasMessageThat().isEqualTo("FallbackJsonAdapter with " + monitor + " cannot make Json Object");
        }
    }
}
