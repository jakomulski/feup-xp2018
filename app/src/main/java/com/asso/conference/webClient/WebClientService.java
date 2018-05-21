package com.asso.conference.webClient;

import com.asso.conference.BuildConfig;
import com.asso.conference.webClient.models.AuthModel;
import com.asso.conference.webClient.models.BeaconModel;
import com.asso.conference.webClient.models.BluetoothDeviceModel;
import com.asso.conference.webClient.models.EventModel;
import com.asso.conference.webClient.models.LoginDataModel;
import com.asso.conference.webClient.models.ResponseModel;
import com.asso.conference.webClient.models.UserModel;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WebClientService {
    @POST("register")
    Call<ResponseModel<AuthModel>> createUser(@Body UserModel user);

    @POST("login")
    Call<ResponseModel<AuthModel>> logIn(@Body LoginDataModel loginDataModel);

    @GET("user/{id}")
    Call<ResponseModel<UserModel>> getUser(@Path("id") String id);

    @POST("event/beacon")
    Call<ResponseModel<String>> sendBeacon(@Body BeaconModel beaconModel);

    @GET("nextEvent")
    Call<ResponseModel<EventModel>> getNextEvent();

    static WebClientService createClient(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WebClientService.class);
    }

    static WebClientService createAuthenticatedClient(final String token){
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request.Builder ongoing = chain.request().newBuilder();
                        ongoing.addHeader("Accept", "application/json;versions=1");

                        ongoing.addHeader("auth", token);

                        return chain.proceed(ongoing.build());
                    }
                })
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        return retrofit.create(WebClientService.class);
    }


}
