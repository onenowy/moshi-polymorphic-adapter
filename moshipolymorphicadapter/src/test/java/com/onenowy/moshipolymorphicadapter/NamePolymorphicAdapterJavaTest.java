package com.onenowy.moshipolymorphicadapter;

import com.google.common.truth.Truth;
import com.onenowy.moshipolymorphicadapter.util.Computer;
import com.onenowy.moshipolymorphicadapter.util.Keyboard;
import com.onenowy.moshipolymorphicadapter.util.Monitor;
import com.onenowy.moshipolymorphicadapter.util.Mouse;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class NamePolymorphicAdapterJavaTest {
    private final NamePolymorphicAdapterFactory<Computer> nameAdapterFactory = NamePolymorphicAdapterFactory.of(
            Computer.class);
    private final NamePolymorphicAdapterFactory<Computer> withSubtype =
            nameAdapterFactory.withSubtype(Monitor.class, "monitorUnique").withSubtype(Mouse.class, "mouseUnique")
                    .withSubtype(Keyboard.class, "keyboardUnique");
    private final NamePolymorphicAdapterFactory<Computer> withSubtypes = nameAdapterFactory.withSubtypes(
            Arrays.asList(Monitor.class, Mouse.class, Keyboard.class),
            Arrays.asList("monitorUnique", "mouseUnique", "keyboardUnique"));
    private final Monitor monitor = new Monitor(1, "test");
    private final Mouse mouse = new Mouse("mouse", "test");
    private final Keyboard keyboard = new Keyboard(true, "test");
    private final String monitorJson = "{\"monitorUnique\":1,\"testValue\":\"test\"}";
    private final String mouseJson = "{\"mouseUnique\":\"mouse\",\"testValue\":\"test\"}";
    private final String keyboardJson = "{\"keyboardUnique\":true,\"testValue\":\"test\"}";

    private JsonAdapter<Computer> getComputerAdapter(JsonAdapter.Factory factory) {
        return (new Moshi.Builder()).add(factory).build().adapter(Computer.class);
    }

    @Test
    public final void toJson() {
        JsonAdapter<Computer> adapter = getComputerAdapter(withSubtype);
        Truth.assertThat(adapter.toJson(monitor)).isEqualTo(monitorJson);
        Truth.assertThat(adapter.toJson(mouse)).isEqualTo(mouseJson);
        Truth.assertThat(adapter.toJson(keyboard)).isEqualTo(keyboardJson);
        adapter = getComputerAdapter(withSubtypes);
        Truth.assertThat(adapter.toJson(monitor)).isEqualTo(monitorJson);
        Truth.assertThat(adapter.toJson(mouse)).isEqualTo(mouseJson);
        Truth.assertThat(adapter.toJson(keyboard)).isEqualTo(keyboardJson);
    }

    @Test
    public final void fromJson() throws IOException {
        JsonAdapter<Computer> adapter = getComputerAdapter(withSubtype);
        Truth.assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor);
        Truth.assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse);
        Truth.assertThat(adapter.fromJson(keyboardJson)).isEqualTo(keyboard);
        adapter = getComputerAdapter(withSubtypes);
        Truth.assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor);
        Truth.assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse);
        Truth.assertThat(adapter.fromJson(keyboardJson)).isEqualTo(keyboard);
    }

    @Test
    public final void unregisteredSubtype() throws IOException {
        JsonAdapter<Computer> adapter = getComputerAdapter(nameAdapterFactory);

        try {
            adapter.toJson(monitor);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Truth.assertThat(e).hasMessageThat().isEqualTo(
                    "Expected one of [] but found " + monitor + ", a " + monitor.getClass() + ". Register this " +
                            "subtype.");
        }

        try {
            adapter.fromJson(monitorJson);
            Assert.fail();
        } catch (JsonDataException e) {
            Truth.assertThat(e).hasMessageThat().isEqualTo("No matching Field names for []");
        }

        adapter = getComputerAdapter(nameAdapterFactory.withSubtype(Monitor.class, "test"));
        Truth.assertThat(adapter.toJson(monitor)).isEqualTo(monitorJson);

        try {
            adapter.fromJson(monitorJson);
            Assert.fail();
        } catch (JsonDataException e) {
            Truth.assertThat(e).hasMessageThat().isEqualTo("No matching Field names for [test]");
        }
    }

    @Test
    public final void defaultValue() throws IOException {
        JsonAdapter<Computer> adapter = getComputerAdapter(nameAdapterFactory.withDefaultValue(monitor));
        Truth.assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor);
        Truth.assertThat(adapter.fromJson(mouseJson)).isEqualTo(monitor);
        Truth.assertThat(adapter.fromJson(keyboardJson)).isEqualTo(monitor);

        try {
            adapter.toJson(monitor);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Truth.assertThat(e).hasMessageThat()
                    .isEqualTo("FallbackJsonAdapter with " + monitor + " cannot make Json Object");
        }
    }

    @Test
    public final void notUniqueSubtype() throws IOException {
        NamePolymorphicAdapterFactory<Computer> notUniqueWithSubtype = nameAdapterFactory.withSubtype(Monitor.class,
                "monitorUnique").withSubtype(Mouse.class, "mouseUnique").withSubtype(Monitor.class, "keyboardUnique");
        JsonAdapter<Computer> adapter = getComputerAdapter(notUniqueWithSubtype);
        Truth.assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor);
        Truth.assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse);
        Truth.assertThat(adapter.fromJson("{\"keyboardUnique\":1,\"testValue\":\"test\"}"))
                .isEqualTo(new Monitor(null, "test"));
        Truth.assertThat(adapter.toJson(new Monitor(1, "test")))
                .isEqualTo("{\"monitorUnique\":1,\"testValue\":\"test\"}");
    }

    @Test
    public final void uniqueLabel() {
        try {
            nameAdapterFactory.withSubtype(Monitor.class, "monitorUnique").withSubtype(Mouse.class, "mouseUnique")
                    .withSubtype(Keyboard.class, "monitorUnique");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Truth.assertThat(e).hasMessageThat().isEqualTo("monitorUnique must be unique");
        }

        try {
            nameAdapterFactory.withSubtypes(Arrays.asList(Monitor.class, Mouse.class, Keyboard.class),
                    Arrays.asList("monitorUnique", "mouseUnique", "monitorUnique"));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Truth.assertThat(e).hasMessageThat()
                    .isEqualTo("The label name for " + Computer.class.getSimpleName() + " must be unique");
        }
    }

    @Test
    public final void notEqualNumberWithSubtypes() {
        try {
            nameAdapterFactory.withSubtypes(Arrays.asList(Monitor.class, Mouse.class, Keyboard.class),
                    Arrays.asList("monitorUnique", "mouseUnique"));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Truth.assertThat(e).hasMessageThat().isEqualTo(
                    "The number of label names for " + Computer.class.getSimpleName() + " is different from subtypes");
        }
    }

    @Test
    public final void javaInterfaceWithCustomName() throws IOException {
        JsonAdapter<ComputerJava> adapter = (new Moshi.Builder()).add(
                        NamePolymorphicAdapterFactory.of(ComputerJava.class)
                                .withSubtypes(Arrays.asList(MouseJava.class, KeyboardJava.class, MonitorJava.class),
                                        Arrays.asList("unique_mouse", "uniqueKeyboard", "uniqueMonitor"))).build()
                .adapter(ComputerJava.class);
        final MonitorJava monitorJava = new MonitorJava("test");
        final MouseJava mouseJava = new MouseJava(Long.MAX_VALUE);
        final KeyboardJava keyboardJava = new KeyboardJava(true);
        final String monitorJavaJson = "{\"uniqueMonitor\":\"test\"}";
        final String mouseJavaJson = "{\"unique_mouse\":" + Long.MAX_VALUE + "}";
        final String keyboardJavaJson = "{\"uniqueKeyboard\":true}";

        Truth.assertThat(adapter.toJson(monitorJava)).isEqualTo(monitorJavaJson);
        Truth.assertThat(adapter.toJson(mouseJava)).isEqualTo(mouseJavaJson);
        Truth.assertThat(adapter.toJson(keyboardJava)).isEqualTo(keyboardJavaJson);
        Truth.assertThat(adapter.fromJson(monitorJavaJson)).isEqualTo(monitorJava);
        Truth.assertThat(adapter.fromJson(mouseJavaJson)).isEqualTo(mouseJava);
        Truth.assertThat(adapter.fromJson(keyboardJavaJson)).isEqualTo(keyboardJava);
    }
}
