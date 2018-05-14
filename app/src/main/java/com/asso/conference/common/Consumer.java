package com.asso.conference.common;

//SDK SUPPORT
public interface Consumer<T> {
    void consume(T value);
}
