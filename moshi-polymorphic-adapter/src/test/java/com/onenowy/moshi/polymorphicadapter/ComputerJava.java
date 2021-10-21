package com.onenowy.moshi.polymorphicadapter;

import com.squareup.moshi.Json;

import java.util.Objects;

public interface ComputerJava {
}

class MouseJava implements ComputerJava {
    @Json(name = "unique_mouse")
    Long uniqueMouse;

    public MouseJava(Long uniqueMouse) {
        this.uniqueMouse = uniqueMouse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MouseJava mouseJava = (MouseJava) o;

        return Objects.equals(uniqueMouse, mouseJava.uniqueMouse);
    }

    @Override
    public int hashCode() {
        return uniqueMouse != null ? uniqueMouse.hashCode() : 0;
    }
}

class KeyboardJava implements ComputerJava {
    Boolean uniqueKeyboard;

    public KeyboardJava(Boolean uniqueKeyboard) {
        this.uniqueKeyboard = uniqueKeyboard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyboardJava that = (KeyboardJava) o;

        return Objects.equals(uniqueKeyboard, that.uniqueKeyboard);
    }

    @Override
    public int hashCode() {
        return uniqueKeyboard != null ? uniqueKeyboard.hashCode() : 0;
    }
}

class MonitorJava implements ComputerJava {
    String uniqueMonitor;

    public MonitorJava(String uniqueMonitor) {
        this.uniqueMonitor = uniqueMonitor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MonitorJava that = (MonitorJava) o;

        return Objects.equals(uniqueMonitor, that.uniqueMonitor);
    }

    @Override
    public int hashCode() {
        return uniqueMonitor != null ? uniqueMonitor.hashCode() : 0;
    }
}