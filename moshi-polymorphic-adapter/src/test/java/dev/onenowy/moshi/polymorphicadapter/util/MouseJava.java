package dev.onenowy.moshi.polymorphicadapter.util;

import com.squareup.moshi.Json;

import java.util.Objects;

public class MouseJava implements ComputerJava {
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
