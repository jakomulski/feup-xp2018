package com.asso.conference.backgroundServices;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.asso.conference.db.BeaconQueue;
import com.asso.conference.webClient.BookmarkCallback;
import com.asso.conference.webClient.UserService;

import java.io.IOException;

public class BeaconQueuePopService extends IntentService{

    BeaconQueue beaconQueue = BeaconQueue.INSTANCE;
    UserService userService = UserService.INSTANCE;

    private boolean isSending = false;
    private boolean sendingWasSuccess = true;

    public int THREAD_SLEEP = 10000;


    public BeaconQueuePopService() {
        super("Collector");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Beacon Queue Pop service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while(true) {
            try {
                if(!isNetworkAvailable() || !isSending || !sendingWasSuccess || beaconQueue.isEmpty()){
                    Thread.sleep(THREAD_SLEEP);
                }
                if (isNetworkAvailable() && !isSending && !beaconQueue.isEmpty()) {
                    isSending = true;
                    try {
                        userService.sendBeacon(beaconQueue.peek(), new BookmarkCallback<String>() {
                            @Override
                            public void onSuccess(String value) {
                                try {
                                    beaconQueue.pop();
                                } catch (IOException e) {
                                }
                                isSending = false;
                                sendingWasSuccess = true;
                            }

                            @Override
                            public void onError(String message) {
                                isSending = false;
                                sendingWasSuccess = false;
                            }
                        });
                    } catch (IOException e) {
                        isSending = false;
                        sendingWasSuccess = false;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
