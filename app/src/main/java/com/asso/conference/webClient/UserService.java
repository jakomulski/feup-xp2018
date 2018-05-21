package com.asso.conference.webClient;

import android.os.Debug;
import android.util.Log;

import com.asso.conference.BuildConfig;
import com.asso.conference.bluetooth.BluetoothDevice;
import com.asso.conference.db.AuthDBModel;
import com.asso.conference.webClient.models.AuthModel;
import com.asso.conference.webClient.models.BeaconModel;
import com.asso.conference.webClient.models.BluetoothDeviceModel;
import com.asso.conference.webClient.models.EventModel;
import com.asso.conference.webClient.models.LoginDataModel;
import com.asso.conference.webClient.models.ResponseModel;
import com.asso.conference.webClient.models.UserModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public enum UserService {
    INSTANCE;

    Retrofit retrofit = null;

    WebClientService service;
    WebClientService gitHubService;

    UserService(){
        if(AuthDBModel.exists()){
            AuthDBModel authDBModel = AuthDBModel.getFirst();
            createAuthenticatedClient(authDBModel.key);
        }
        else
            createClient();

        service = retrofit.create(WebClientService.class);

        Retrofit gitHubRetrofit = new Retrofit.Builder()
                .baseUrl("https://raw.githubusercontent.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        gitHubRetrofit.create(WebClientService.class);
        gitHubService = gitHubRetrofit.create(WebClientService.class);
    }

    public void sendBeacon(BeaconModel beaconModel, final BookmarkCallback<String> callback){
        Call<ResponseModel<String>> call = service.sendBeacon(beaconModel);
        call.enqueue(new Callback<ResponseModel<String>>() {
            @Override
            public void onResponse(Call<ResponseModel<String>> call, Response<ResponseModel<String>> response) {
                if(response.isSuccessful())
                    callback.onSuccess(response.body().success);
                else{
                    try {
                        ResponseModel r = new GsonBuilder().create().fromJson(response.errorBody().string(), ResponseModel.class);
                        callback.onError(r.failure);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseModel<String>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getBluetoothDevices(final BookmarkCallback<BluetoothDeviceModel[]> callback){

        /*BluetoothDeviceModel midi = new BluetoothDeviceModel("C4:BE:84:49:DD:7E", 1, 0.3f, 0.1f);
        BluetoothDeviceModel sensorTag = new BluetoothDeviceModel("B0:B4:48:BC:E5:82", 2,0.5f,0.9f);
        BluetoothDeviceModel bt100 = new BluetoothDeviceModel("A0:E9:DB:08:49:C2", 3,0.9f,0.5f);

        BluetoothDeviceModel[] bts = new BluetoothDeviceModel[] {midi, sensorTag, bt100};*/
        Call<ResponseModel<BluetoothDeviceModel[]>> call = gitHubService.getBluetoothDevices();
        call.enqueue(new Callback<ResponseModel<BluetoothDeviceModel[]>>() {
            @Override
            public void onResponse(Call<ResponseModel<BluetoothDeviceModel[]>> call, Response<ResponseModel<BluetoothDeviceModel[]>> response) {
                BluetoothDeviceModel[] bts = response.body().bluetoothDevices;
                Log.d("BT DEVICES", bts[0].address);
                callback.onSuccess(bts); // TODO REMOVE HARDCODED
                /*if(response.isSuccessful())
                    callback.onSuccess(response.body().success);
                else{
                    try {
                        ResponseModel r = new GsonBuilder().create().fromJson(response.errorBody().string(), ResponseModel.class);
                        callback.onError(r.failure);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/

            }

            @Override
            public void onFailure(Call<ResponseModel<BluetoothDeviceModel[]>> call, Throwable t) {
                callback.onError(t.getMessage());
                //Log.d("BT DEVICES", t.getMessage());
                //callback.onSuccess(bts); // TODO REMOVE HARDCODED
            }
        });
    }

    public void getNextEvent(final BookmarkCallback<EventModel> callback){

        EventModel nextEvent = new EventModel("Coffee Break", "Time for networking", 10);

        Call<ResponseModel<EventModel>> call = service.getNextEvent();
        call.enqueue(new Callback<ResponseModel<EventModel>>() {
            @Override
            public void onResponse(Call<ResponseModel<EventModel>> call, Response<ResponseModel<EventModel>> response) {
                callback.onSuccess(nextEvent); // TODO REMOVE HARDCODED
                /*if(response.isSuccessful())
                    callback.onSuccess(response.body().success);
                else{
                    try {
                        ResponseModel r = new GsonBuilder().create().fromJson(response.errorBody().string(), ResponseModel.class);
                        callback.onError(r.failure);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/

            }

            @Override
            public void onFailure(Call<ResponseModel<EventModel>> call, Throwable t) {
                //callback.onError(t.getMessage());

                callback.onSuccess(nextEvent); // TODO REMOVE HARDCODED
            }
        });
    }


    public boolean isAuthenticated(BookmarkCallback<UserModel> callback){
        if(AuthDBModel.exists()){
            AuthDBModel authDBModel = AuthDBModel.getFirst();
            final Call<ResponseModel<UserModel>> call = service.getUser(authDBModel.userId);
            call.enqueue(new Callback<ResponseModel<UserModel>>() {
                @Override
                public void onResponse(Call<ResponseModel<UserModel>> call, Response<ResponseModel<UserModel>> response) {
                    if(response.isSuccessful())
                        callback.onSuccess(response.body().success);
                    else {
                        callback.onError(response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel<UserModel>> call, Throwable t) {
                    callback.onError(t.getMessage());
                }
            });
            return true;
            /*try {
                if(call.execute().isSuccessful())
                    return true;
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
        return false;
    }

    private void createClient(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofit.create(WebClientService.class);
    }

    public void createAuthenticatedClient(final String token){
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
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        retrofit.create(WebClientService.class);
        service = retrofit.create(WebClientService.class);
    }

    public void signUp(UserModel user, final BookmarkCallback<AuthModel> callback) {
        Call<ResponseModel<AuthModel>> call = service.createUser(user);
        call.enqueue(new Callback<ResponseModel<AuthModel>>() {
            @Override
            public void onResponse(Call<ResponseModel<AuthModel>> call, Response<ResponseModel<AuthModel>> response) {
                if (response.isSuccessful())
                    callback.onSuccess(response.body().success);
                else{
                    try {
                        ResponseModel r = new GsonBuilder().create().fromJson(response.errorBody().string(), ResponseModel.class);
                        callback.onError(r.failure);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<AuthModel>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void logIn(String username, String password, final BookmarkCallback<AuthModel> callback){
        Call<ResponseModel<AuthModel>> call = service.logIn(new LoginDataModel(username, password));
        call.enqueue(new Callback<ResponseModel<AuthModel>>() {
            @Override
            public void onResponse(Call<ResponseModel<AuthModel>> call, Response<ResponseModel<AuthModel>> response) {
                if(response.isSuccessful())
                    callback.onSuccess(response.body().success);
                else{
                    try {
                        ResponseModel r = new GsonBuilder().create().fromJson(response.errorBody().string(), ResponseModel.class);
                        callback.onError(r.failure);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseModel<AuthModel>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }




}
