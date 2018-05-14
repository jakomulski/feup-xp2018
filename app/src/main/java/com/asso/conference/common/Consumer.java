package com.asso.conference.common;

public interface Consumer<T> {
    void consume(T value);
}
