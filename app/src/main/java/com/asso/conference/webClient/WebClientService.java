package com.asso.conference.webClient;

import com.asso.conference.webClient.models.AuthModel;
import com.asso.conference.webClient.models.LoginDataModel;
import com.asso.conference.webClient.models.ResponseModel;
import com.asso.conference.webClient.models.UserModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WebClientService {
    @POST("register")
    Call<UserModel> createUser(@Body UserModel user);

    //@FormUrlEncoded
    @POST("login")
    Call<ResponseModel<AuthModel>> logIn(@Body LoginDataModel loginDataModel);

    @GET("user/{id}")
    Call<ResponseModel<UserModel>> getUser(@Path("id") String id);

}
