package dev.onenowy.moshi.polymorphicadapter;

import java.util.Objects;

public class KeyboardJava implements ComputerJava {
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
