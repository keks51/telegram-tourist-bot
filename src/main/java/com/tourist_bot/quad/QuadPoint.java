package com.tourist_bot.quad;

import java.util.Objects;


public class QuadPoint<T> {
    public final double x;
    public final double y;
    public final T value;

    public QuadPoint(double x, double y, T value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    @Override
    public String toString() {
        return "[x:" + x + ",y:" + y + ",v:" + value + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuadPoint<?> point = (QuadPoint<?>) o;
        return Double.compare(x, point.x) == 0 && Double.compare(y, point.y) == 0 && Objects.equals(value, point.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, value);
    }

}
