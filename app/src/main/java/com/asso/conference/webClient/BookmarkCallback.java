package com.asso.conference.webClient;

public interface BookmarkCallback<T>{
    void onSuccess(T value);
    void onError(String message);
}