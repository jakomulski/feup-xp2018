package com.asso.conference.webClient;

import com.asso.conference.common.DateDeserializer;
import com.asso.conference.webClient.models.AuthModel;
import com.asso.conference.webClient.models.BeaconModel;
import com.asso.conference.webClient.models.BluetoothDeviceModel;
import com.asso.conference.webClient.models.EventModel;
import com.asso.conference.webClient.models.LoginDataModel;
import com.asso.conference.webClient.models.ResponseModel;
import com.asso.conference.webClient.models.UserModel;
import com.asso.conference.webClient.models.XpEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GithubService {
    @GET("telmobarros/xp-2018-android/master/configValues.json")
    Call<ResponseModel<BluetoothDeviceModel[]>> getBluetoothDevices();

    @GET("telmobarros/xp-2018-android/master/xpevents.json")
    Call<List<XpEvent>> getXpEvents();


    static GithubService getClient(){
        Retrofit gitHubRetrofit = new Retrofit.Builder()
                .baseUrl("https://raw.githubusercontent.com")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create()))
                .build();
        return gitHubRetrofit.create(GithubService.class);
    }
}
