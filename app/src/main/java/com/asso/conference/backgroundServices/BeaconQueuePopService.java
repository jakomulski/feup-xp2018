package com.asso.conference.backgroundServices;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;

import com.asso.conference.db.BeaconQueue;
import com.asso.conference.webClient.BookmarkCallback;
import com.asso.conference.webClient.UserService;

import java.io.IOException;

public class BeaconQueuePopService extends IntentService{

    BeaconQueue beaconQueue = BeaconQueue.INSTANCE;
    UserService userService = UserService.INSTANCE;

    private boolean isSending = false;

    public BeaconQueuePopService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while(true) {
            try {
                Thread.sleep(10000);
                if (isNetworkAvailable()&& isSending == false) {
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
                            }

                            @Override
                            public void onError(String message) {
                                isSending = false;
                            }
                        });
                    } catch (IOException e) {
                        isSending = false;
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
