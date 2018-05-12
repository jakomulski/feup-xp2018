package com.asso.conference.webClient;

import com.asso.conference.webClient.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WebClientService {
    @POST("register")
    Call<User> createUser(@Body User user);
}
