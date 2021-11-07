package dev.onenowy.moshipolymorphicadapter;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import dev.onenowy.moshi.polymorphicadapter.util.Computer;
import dev.onenowy.moshi.polymorphicadapter.util.Keyboard;
import dev.onenowy.moshi.polymorphicadapter.util.Monitor;
import dev.onenowy.moshi.polymorphicadapter.util.Mouse;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static com.google.common.truth.Truth.assertThat;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ValuePolymorphicAdapterJavaTest {
    ValuePolymorphicAdapterFactory<Computer, Integer> intFactory = ValuePolymorphicAdapterFactory.of(Computer.class,
                    "typeInt", int.class).withSubtype(Monitor.class, Computer.ComTypeInt.Monitor.getValue())
            .withSubtype(Keyboard.class, Computer.ComTypeInt.Keyboard.getValue())
            .withSubtype(Mouse.class, Computer.ComTypeInt.Mouse.getValue());
    ValuePolymorphicAdapterFactory<Computer, String> stringFactory = ValuePolymorphicAdapterFactory.of(Computer.class,
                    "typeString", String.class).withSubtype(Monitor.class, Computer.ComTypeString.Monitor.getValue())
            .withSubtype(Keyboard.class, Computer.ComTypeString.Keyboard.getValue())
            .withSubtype(Mouse.class, Computer.ComTypeString.Mouse.getValue());
    ValuePolymorphicAdapterFactory<Computer, Double> doubleFactory = ValuePolymorphicAdapterFactory.of(Computer.class,
                    "typeDouble", double.class).withSubtype(Monitor.class, Computer.ComTypeDouble.Monitor.getValue())
            .withSubtype(Keyboard.class, Computer.ComTypeDouble.Keyboard.getValue())
            .withSubtype(Mouse.class, Computer.ComTypeDouble.Mouse.getValue());
    ValuePolymorphicAdapterFactory<Computer, Long> longFactory = ValuePolymorphicAdapterFactory.of(Computer.class,
                    "typeLong", long.class).withSubtype(Monitor.class, Computer.ComTypeLong.Monitor.getValue())
            .withSubtype(Keyboard.class, Computer.ComTypeLong.Keyboard.getValue())
            .withSubtype(Mouse.class, Computer.ComTypeLong.Mouse.getValue());
    ValuePolymorphicAdapterFactory<Computer, Boolean> boolFactory = ValuePolymorphicAdapterFactory.of(Computer.class,
                    "typeBool", boolean.class)
            .withSubtype(Monitor.class, true)
            .withSubtype(Mouse.class, false);
    Monitor monitor = new Monitor(1, "test");
    Mouse mouse = new Mouse("mouse", "test");
    Keyboard keyboard = new Keyboard(true, "test");
    String monitorJson = "{\"typeBool\":true, \"typeInt\":1,\"typeString\":\"1\",\"typeDouble\":5.0," +
            "\"typeLong\":9223372036854775805," +
            "\"monitorUnique\":1,\"testValue\":\"test\"}";
    String mouseJson = "{\"typeBool\":false, \"typeInt\":2,\"typeString\":\"2\",\"typeDouble\":10000.1," +
            "\"typeLong\":9223372036854775806," +
            "\"mouseUnique\":\"mouse\",\"testValue\":\"test\"}";
    String keyboardJson = "{\"typeInt\":3,\"typeString\":\"3\",\"typeDouble\":1.7976931348623157E308," +
            "\"typeLong\":9223372036854775807,\"keyboardUnique\":true,\"testValue\":\"test\"}";

    private JsonAdapter<Computer> getComputerAdapter(JsonAdapter.Factory factory) {
        return (new Moshi.Builder()).add(factory).build().adapter(Computer.class);
    }

    @Test
    public final void toJson() {
        JsonAdapter<Computer> adapter = getComputerAdapter(intFactory);
        assertThat(adapter.toJson(monitor)).contains("\"typeInt\":1");
        assertThat(adapter.toJson(mouse)).contains("\"typeInt\":2");
        assertThat(adapter.toJson(keyboard)).contains("\"typeInt\":3");
        adapter = getComputerAdapter(stringFactory);
        assertThat(adapter.toJson(monitor)).contains("\"typeString\":\"1\"");
        assertThat(adapter.toJson(mouse)).contains("\"typeString\":\"2\"");
        assertThat(adapter.toJson(keyboard)).contains("\"typeString\":\"3\"");
        adapter = getComputerAdapter(doubleFactory);
        assertThat(adapter.toJson(monitor)).contains("\"typeDouble\":5.0");
        assertThat(adapter.toJson(mouse)).contains("\"typeDouble\":10000.1");
        assertThat(adapter.toJson(keyboard)).contains("\"typeDouble\":1.7976931348623157E308");
        adapter = getComputerAdapter(longFactory);
        assertThat(adapter.toJson(monitor)).contains("\"typeLong\":9223372036854775805");
        assertThat(adapter.toJson(mouse)).contains("\"typeLong\":9223372036854775806");
        assertThat(adapter.toJson(keyboard)).contains("\"typeLong\":9223372036854775807");
        adapter = getComputerAdapter(boolFactory);
        assertThat(adapter.toJson(monitor)).contains("\"typeBool\":true");
        assertThat(adapter.toJson(mouse)).contains("\"typeBool\":false");
    }

    @Test
    public final void fromJson() throws IOException {
        JsonAdapter<Computer> adapter = getComputerAdapter(intFactory);
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor);
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse);
        assertThat(adapter.fromJson(keyboardJson)).isEqualTo(keyboard);
        adapter = getComputerAdapter(stringFactory);
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor);
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse);
        assertThat(adapter.fromJson(keyboardJson)).isEqualTo(keyboard);
        adapter = getComputerAdapter(doubleFactory);
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor);
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse);
        assertThat(adapter.fromJson(keyboardJson)).isEqualTo(keyboard);
        adapter = getComputerAdapter(longFactory);
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor);
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse);
        assertThat(adapter.fromJson(keyboardJson)).isEqualTo(keyboard);
        adapter = getComputerAdapter(boolFactory);
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor);
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse);
    }

    @Test
    public final void unregisteredSubtype() throws IOException {
        ValuePolymorphicAdapterFactory<Computer, Integer> propertyValueAdapterFactory =
                ValuePolymorphicAdapterFactory.Companion.of(
                        Computer.class, "typeInt", Integer.TYPE);
        JsonAdapter<Computer> adapter = getComputerAdapter(propertyValueAdapterFactory);

        try {
            adapter.toJson(monitor);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().isEqualTo(
                    "Expected one of [] but found " + monitor + ", a " + monitor.getClass() + ". Register this " +
                            "subtype.");
        }

        try {
            adapter.fromJson(monitorJson);
            Assert.fail();
        } catch (JsonDataException e) {
            assertThat(e).hasMessageThat().isEqualTo(
                    "Expected one of [] for key 'typeInt' but found '1'. Register a subtype for this label.");
        }

        adapter = getComputerAdapter(
                propertyValueAdapterFactory.withSubtype(Keyboard.class, Computer.ComTypeInt.Keyboard.getValue()));

        try {
            adapter.toJson(monitor);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().isEqualTo("Expected one of " + Collections.singletonList(
                    Keyboard.class) + " but found " + monitor + ", a " + monitor.getClass() + ". Register " + "this " +
                    "subtype.");
        }

        try {
            adapter.fromJson(monitorJson);
            Assert.fail();
        } catch (JsonDataException e) {
            assertThat(e).hasMessageThat().isEqualTo("Expected one of " + Collections.singletonList(
                    Computer.ComTypeInt.Keyboard.getValue()) + " for key 'typeInt' but found '1'. " + "Register a " + "subtype for this label.");
        }
    }

    @Test
    public final void unregisteredLabelKey() throws IOException {
        ValuePolymorphicAdapterFactory<Computer, Integer> propertyValueAdapterFactory =
                ValuePolymorphicAdapterFactory.of(
                                Computer.class, "wrongKey", int.class)
                        .withSubtype(Monitor.class, Computer.ComTypeInt.Monitor.getValue())
                        .withSubtype(Keyboard.class, Computer.ComTypeInt.Keyboard.getValue())
                        .withSubtype(Mouse.class, Computer.ComTypeInt.Mouse.getValue());
        JsonAdapter<Computer> adapter = getComputerAdapter(propertyValueAdapterFactory);
        assertThat(adapter.toJson(monitor)).contains("\"wrongKey\":1");
        assertThat(adapter.toJson(mouse)).contains("\"wrongKey\":2");
        assertThat(adapter.toJson(keyboard)).contains("\"wrongKey\":3");

        try {
            adapter.fromJson(monitorJson);
            Assert.fail();
        } catch (JsonDataException e) {
            assertThat(e).hasMessageThat().isEqualTo("Missing label for wrongKey");
        }
    }

    @Test
    public final void defaultValue() throws IOException {
        ValuePolymorphicAdapterFactory<Computer, Integer> propertyValueAdapterFactory =
                ValuePolymorphicAdapterFactory.of(
                        Computer.class, "typeInt", int.class).withDefaultValue(monitor);
        JsonAdapter<Computer> adapter = getComputerAdapter(propertyValueAdapterFactory);
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor);
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(monitor);
        assertThat(adapter.fromJson(keyboardJson)).isEqualTo(monitor);

        try {
            adapter.toJson(keyboard);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat()
                    .isEqualTo("FallbackJsonAdapter with " + monitor + " cannot make Json Object");
        }
        ValuePolymorphicAdapterFactory<Computer, Integer> propertyValueAdapterFactoryWithWrongLabelKey =
                ValuePolymorphicAdapterFactory.of(
                        Computer.class, "typeWrong", int.class).withDefaultValue(monitor);
        adapter = getComputerAdapter(propertyValueAdapterFactoryWithWrongLabelKey);
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor);
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(monitor);
        assertThat(adapter.fromJson(keyboardJson)).isEqualTo(monitor);

        try {
            adapter.toJson(keyboard);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat()
                    .isEqualTo("FallbackJsonAdapter with " + monitor + " cannot make Json Object");
        }
    }

    @Test
    public final void notUniqueSubtype() throws IOException {
        ValuePolymorphicAdapterFactory<Computer, Integer> notUnique = ValuePolymorphicAdapterFactory.of(Computer.class,
                        "typeInt", int.class).withSubtype(Monitor.class, Computer.ComTypeInt.Monitor.getValue())
                .withSubtype(Monitor.class, Computer.ComTypeInt.Keyboard.getValue())
                .withSubtype(Mouse.class, Computer.ComTypeInt.Mouse.getValue());
        JsonAdapter<Computer> adapter = getComputerAdapter(notUnique);
        assertThat(adapter.fromJson(monitorJson)).isEqualTo(monitor);
        assertThat(adapter.fromJson(mouseJson)).isEqualTo(mouse);
        assertThat(adapter.fromJson("{\"typeInt\":3,\"monitorUnique\":1,\"testValue\":\"test\"}"))
                .isEqualTo(monitor);
        assertThat(adapter.toJson(new Monitor(1, "test")))
                .isEqualTo("{\"typeInt\":1,\"monitorUnique\":1,\"testValue\":\"test\"}");
    }

    @Test
    public final void uniqueLabel() {
        try {
            ValuePolymorphicAdapterFactory.of(Computer.class, "typeInt", int.class)
                    .withSubtype(Monitor.class, Computer.ComTypeInt.Monitor.getValue())
                    .withSubtype(Keyboard.class, Computer.ComTypeInt.Monitor.getValue())
                    .withSubtype(Mouse.class, Computer.ComTypeInt.Mouse.getValue());
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().isEqualTo("The value label must be unique");
        }

        try {
            ValuePolymorphicAdapterFactory.of(Computer.class, "typeInt", Integer.TYPE)
                    .withSubtypes(Arrays.asList(Monitor.class, Keyboard.class, Mouse.class),
                            Arrays.asList(Computer.ComTypeInt.Monitor.getValue(),
                                    Computer.ComTypeInt.Monitor.getValue(), Computer.ComTypeInt.Monitor.getValue()));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat()
                    .isEqualTo("The value for " + Computer.class.getSimpleName() + " must be unique");
        }
    }

    @Test
    public final void notEqualNumberWithSubtypes() {
        try {
            ValuePolymorphicAdapterFactory.Companion.of(Computer.class, "typeInt", int.class)
                    .withSubtypes(Arrays.asList(Monitor.class, Keyboard.class, Mouse.class),
                            Collections.singletonList(Computer.ComTypeInt.Monitor.getValue()));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().isEqualTo(
                    "The number of values for " + Computer.class.getSimpleName() + " is different from subtypes");
        }
    }

    @Test
    public final void javaInterfaceWithCustomName() throws IOException {
        JsonAdapter<ComputerJava> adapter = (new Moshi.Builder()).add(
                        ValuePolymorphicAdapterFactory.of(ComputerJava.class, "type", int.class)
                                .withSubtypes(Arrays.asList(MouseJava.class, KeyboardJava.class, MonitorJava.class),
                                        Arrays.asList(2, 3, 1))).build()
                .adapter(ComputerJava.class);
        final MonitorJava monitorJava = new MonitorJava("test");
        final MouseJava mouseJava = new MouseJava(Long.MAX_VALUE);
        final KeyboardJava keyboardJava = new KeyboardJava(true);
        final String monitorJavaJson = "{\"type\":1,\"uniqueMonitor\":\"test\"}";
        final String mouseJavaJson = "{\"type\":2,\"unique_mouse\":" + Long.MAX_VALUE + "}";
        final String keyboardJavaJson = "{\"type\":3,\"uniqueKeyboard\":true}";

        assertThat(adapter.toJson(monitorJava)).isEqualTo(monitorJavaJson);
        assertThat(adapter.toJson(mouseJava)).isEqualTo(mouseJavaJson);
        assertThat(adapter.toJson(keyboardJava)).isEqualTo(keyboardJavaJson);
        assertThat(adapter.fromJson(monitorJavaJson)).isEqualTo(monitorJava);
        assertThat(adapter.fromJson(mouseJavaJson)).isEqualTo(mouseJava);
        assertThat(adapter.fromJson(keyboardJavaJson)).isEqualTo(keyboardJava);
    }

    @Test
    public final void notSupportedType() {
        try {
            ValuePolymorphicAdapterFactory.Companion.of(Computer.class, "typeInt", byte.class);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().isEqualTo(byte.class.getSimpleName() + " is not a supported type");
        }
    }
}
