package dev.onenowy.moshi.polymorphicadapter;

import java.util.Objects;

public class MonitorJava implements ComputerJava {
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
