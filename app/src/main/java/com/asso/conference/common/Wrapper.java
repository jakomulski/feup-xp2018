package com.asso.conference.common;


public class Wrapper<T> {
    private T value;

    public void setValue(T value) {
        this.value = value;
    }

    public Wrapper() {
    }

    public void ifPresent(Consumer<T> consumer){
        if(this.value != null)
            consumer.consume(value);
    }

    public Wrapper(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

}
