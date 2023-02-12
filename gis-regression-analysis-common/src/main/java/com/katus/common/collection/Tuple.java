package com.katus.common.collection;

import java.util.Objects;

/**
 * @author SUN Katus
 * @version 1.0, 2022-11-23
 */
public class Tuple<V1, V2> {
    private V1 _1;
    private V2 _2;

    public Tuple() {
    }

    public Tuple(V1 _1, V2 _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public V1 _1() {
        return _1;
    }

    public void _1(V1 _1) {
        this._1 = _1;
    }

    public V2 _2() {
        return _2;
    }

    public void _2(V2 _2) {
        this._2 = _2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(_1, tuple._1) && Objects.equals(_2, tuple._2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2);
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "_1=" + _1 +
                ", _2=" + _2 +
                '}';
    }
}
