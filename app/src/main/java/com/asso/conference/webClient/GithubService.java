package com.asso.conference.webClient;

import com.asso.conference.webClient.models.AuthModel;
import com.asso.conference.webClient.models.BeaconModel;
import com.asso.conference.webClient.models.BluetoothDeviceModel;
import com.asso.conference.webClient.models.EventModel;
import com.asso.conference.webClient.models.LoginDataModel;
import com.asso.conference.webClient.models.ResponseModel;
import com.asso.conference.webClient.models.UserModel;

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

    static GithubService getClient(){
        Retrofit gitHubRetrofit = new Retrofit.Builder()
                .baseUrl("https://raw.githubusercontent.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return gitHubRetrofit.create(GithubService.class);
    }
}
