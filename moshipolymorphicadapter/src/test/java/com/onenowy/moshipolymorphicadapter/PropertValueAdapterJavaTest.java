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

public class PropertValueAdapterJavaTest {
    @NotNull
    private final PropertyValueAdapterFactory intFactory;
    @NotNull
    private final PropertyValueAdapterFactory stringFacgtory;
    @NotNull
    private final PropertyValueAdapterFactory doubleFactory;
    @NotNull
    private final PropertyValueAdapterFactory longFactory;
    private final Monitor monitor;
    private final Mouse mouse;
    private final Keyboard keyboard;
    private final String monitorJson;
    private final String mouseJson;
    private final String keyboardJson;

    public PropertValueAdapterJavaTest() {
        this.intFactory = PropertyValueAdapterFactory.Companion.of(Computer.class, "typeInt", Integer.TYPE).withSubType(Monitor.class, Computer.ComTypeInt.Monitor.getValue()).withSubType(Keyboard.class, Computer.ComTypeInt.Keyboard.getValue()).withSubType(Mouse.class, Computer.ComTypeInt.Mouse.getValue());
        this.stringFacgtory = PropertyValueAdapterFactory.Companion.of(Computer.class, "typeString", String.class).withSubType(Monitor.class, Computer.ComTypeString.Monitor.getValue()).withSubType(Keyboard.class, Computer.ComTypeString.Keyboard.getValue()).withSubType(Mouse.class, Computer.ComTypeString.Mouse.getValue());
        this.doubleFactory = PropertyValueAdapterFactory.Companion.of(Computer.class, "typeDouble", Double.TYPE).withSubType(Monitor.class, Computer.ComTypeDouble.Monitor.getValue()).withSubType(Keyboard.class, Computer.ComTypeDouble.Keyboard.getValue()).withSubType(Mouse.class, Computer.ComTypeDouble.Mouse.getValue());
        this.longFactory = PropertyValueAdapterFactory.Companion.of(Computer.class, "typeLong", Long.TYPE).withSubType(Monitor.class, Computer.ComTypeLong.Monitor.getValue()).withSubType(Keyboard.class, Computer.ComTypeLong.Keyboard.getValue()).withSubType(Mouse.class, Computer.ComTypeLong.Mouse.getValue());
        this.monitor = new Monitor(1);
        this.mouse = new Mouse("mouse");
        this.keyboard = new Keyboard(true);
        this.monitorJson = "{\"typeInt\":1,\"typeString\":\"1\",\"typeDouble\":5.0,\"typeLong\":9223372036854775805,\"monitorUnique\":1}";
        this.mouseJson = "{\"typeInt\":2,\"typeString\":\"2\",\"typeDouble\":10000.1,\"typeLong\":9223372036854775806,\"mouseUnique\":\"mouse\"}";
        this.keyboardJson = "{\"typeInt\":3,\"typeString\":\"3\",\"typeDouble\":1.7976931348623157E308,\"typeLong\":9223372036854775807,\"keyboardUnique\":true}";
    }

    @NotNull
    public final PropertyValueAdapterFactory getIntFactory() {
        return this.intFactory;
    }

    @NotNull
    public final PropertyValueAdapterFactory getStringFacgtory() {
        return this.stringFacgtory;
    }

    @NotNull
    public final PropertyValueAdapterFactory getDoubleFactory() {
        return this.doubleFactory;
    }

    @NotNull
    public final PropertyValueAdapterFactory getLongFactory() {
        return this.longFactory;
    }

    private final JsonAdapter getComputerAdapter(JsonAdapter.Factory factory) {
        return (new Moshi.Builder()).add(factory).build().adapter(Computer.class);
    }

    @Test
    public final void toJson() {
        JsonAdapter adapter = this.getComputerAdapter(this.intFactory);
        Truth.assertThat(adapter.toJson(this.monitor)).contains("\"typeInt\":1");
        Truth.assertThat(adapter.toJson(this.mouse)).contains("\"typeInt\":2");
        Truth.assertThat(adapter.toJson(this.keyboard)).contains("\"typeInt\":3");
        adapter = this.getComputerAdapter(this.stringFacgtory);
        Truth.assertThat(adapter.toJson(this.monitor)).contains("\"typeString\":\"1\"");
        Truth.assertThat(adapter.toJson(this.mouse)).contains("\"typeString\":\"2\"");
        Truth.assertThat(adapter.toJson(this.keyboard)).contains("\"typeString\":\"3\"");
        adapter = this.getComputerAdapter(this.doubleFactory);
        Truth.assertThat(adapter.toJson(this.monitor)).contains("\"typeDouble\":5.0");
        Truth.assertThat(adapter.toJson(this.mouse)).contains("\"typeDouble\":10000.1");
        Truth.assertThat(adapter.toJson(this.keyboard)).contains("\"typeDouble\":1.7976931348623157E308");
        adapter = this.getComputerAdapter(this.longFactory);
        Truth.assertThat(adapter.toJson(this.monitor)).contains("\"typeLong\":9223372036854775805");
        Truth.assertThat(adapter.toJson(this.mouse)).contains("\"typeLong\":9223372036854775806");
        Truth.assertThat(adapter.toJson(this.keyboard)).contains("\"typeLong\":9223372036854775807");
    }

    @Test
    public final void fromJson() throws IOException {
        JsonAdapter adapter = this.getComputerAdapter(this.intFactory);
        Truth.assertThat(adapter.fromJson(this.monitorJson)).isEqualTo(this.monitor);
        Truth.assertThat(adapter.fromJson(this.mouseJson)).isEqualTo(this.mouse);
        Truth.assertThat(adapter.fromJson(this.keyboardJson)).isEqualTo(this.keyboard);
        adapter = this.getComputerAdapter(this.stringFacgtory);
        Truth.assertThat(adapter.fromJson(this.monitorJson)).isEqualTo(this.monitor);
        Truth.assertThat(adapter.fromJson(this.mouseJson)).isEqualTo(this.mouse);
        Truth.assertThat(adapter.fromJson(this.keyboardJson)).isEqualTo(this.keyboard);
        adapter = this.getComputerAdapter(this.doubleFactory);
        Truth.assertThat(adapter.fromJson(this.monitorJson)).isEqualTo(this.monitor);
        Truth.assertThat(adapter.fromJson(this.mouseJson)).isEqualTo(this.mouse);
        Truth.assertThat(adapter.fromJson(this.keyboardJson)).isEqualTo(this.keyboard);
        adapter = this.getComputerAdapter(this.longFactory);
        Truth.assertThat(adapter.fromJson(this.monitorJson)).isEqualTo(this.monitor);
        Truth.assertThat(adapter.fromJson(this.mouseJson)).isEqualTo(this.mouse);
        Truth.assertThat(adapter.fromJson(this.keyboardJson)).isEqualTo(this.keyboard);
    }

    @Test
    public final void unregisteredSubtype() throws IOException {
        PropertyValueAdapterFactory propertyValueAdapterFactory = PropertyValueAdapterFactory.Companion.of(Computer.class, "typeInt", Integer.TYPE);
        JsonAdapter adapter = this.getComputerAdapter(propertyValueAdapterFactory);

        try {
            adapter.toJson(this.monitor);
        } catch (IllegalArgumentException var7) {
            System.out.println(var7);
            Truth.assertThat(var7).hasMessageThat().isEqualTo("Expected one of [] but found " + this.monitor + ", a " + this.monitor.getClass() + ". Register this subtype.");
        }

        try {
            adapter.fromJson(this.monitorJson);
        } catch (JsonDataException var6) {
            System.out.println(var6);
            Truth.assertThat(var6).hasMessageThat().isEqualTo("Expected one of [] for key 'typeInt' but found 'null'. Register a subtype for this label.");
        }

        adapter = this.getComputerAdapter(propertyValueAdapterFactory.withSubType(Keyboard.class, Computer.ComTypeInt.Keyboard.getValue()));

        try {
            adapter.toJson(this.monitor);
        } catch (IllegalArgumentException var5) {
            System.out.println(var5);
            Truth.assertThat(var5).hasMessageThat().isEqualTo("Expected one of " + CollectionsKt.listOf(Keyboard.class) + " but found " + this.monitor + ", a " + this.monitor.getClass() + ". Register " + "this subtype.");
        }

        try {
            adapter.fromJson(this.monitorJson);
        } catch (JsonDataException var4) {
            System.out.println(var4);
            Truth.assertThat(var4).hasMessageThat().isEqualTo("Expected one of " + CollectionsKt.listOf(Computer.ComTypeInt.Keyboard.getValue()) + " for key 'typeInt' but found '1'. " + "Register a " + "subtype for this label.");
        }

    }

    @Test
    public final void unresigsterdLableKey() throws IOException {
        PropertyValueAdapterFactory propertyValueAdapterFactory = PropertyValueAdapterFactory.Companion.of(Computer.class, "wrongKey", Integer.TYPE).withSubType(Monitor.class, Computer.ComTypeInt.Monitor.getValue()).withSubType(Keyboard.class, Computer.ComTypeInt.Keyboard.getValue()).withSubType(Mouse.class, Computer.ComTypeInt.Mouse.getValue());
        JsonAdapter adapter = this.getComputerAdapter(propertyValueAdapterFactory);
        Truth.assertThat(adapter.toJson(this.monitor)).contains("\"wrongKey\":1");
        Truth.assertThat(adapter.toJson(this.mouse)).contains("\"wrongKey\":2");
        Truth.assertThat(adapter.toJson(this.keyboard)).contains("\"wrongKey\":3");

        try {
            adapter.fromJson(this.monitorJson);
        } catch (JsonDataException var4) {
            System.out.println(var4);
            Truth.assertThat(var4).hasMessageThat().isEqualTo("Missing label for wrongKey");
        }

    }

    @Test
    public final void defaultValue() throws IOException {
        PropertyValueAdapterFactory propertyValueAdapterFactory =
                PropertyValueAdapterFactory.Companion.of(Computer.class, "typeInt", Integer.TYPE).withDefaultValue(this.monitor);
        JsonAdapter adapter = this.getComputerAdapter(propertyValueAdapterFactory);
        Truth.assertThat(adapter.fromJson(this.monitorJson)).isEqualTo(this.monitor);
        Truth.assertThat(adapter.fromJson(this.mouseJson)).isEqualTo(this.monitor);
        Truth.assertThat(adapter.fromJson(this.keyboardJson)).isEqualTo(this.monitor);

        try {
            adapter.toJson(this.keyboard);
        } catch (IllegalArgumentException var4) {
            System.out.println(var4);
            Truth.assertThat(var4).hasMessageThat().isEqualTo("Expected one of [] but found " + this.keyboard + ", a " + this.keyboard.getClass() + ". Register this subtype.");
        }

    }
}
