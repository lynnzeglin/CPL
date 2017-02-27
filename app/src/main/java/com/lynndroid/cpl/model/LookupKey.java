package com.lynndroid.cpl.model;

/**
 * Created by lynnzeglin on 2/20/17.
 */

public class LookupKey<T> {
    private T t;

    public LookupKey(T t) {
        this.t = t;
    }

    public T get() { return t; }
}
