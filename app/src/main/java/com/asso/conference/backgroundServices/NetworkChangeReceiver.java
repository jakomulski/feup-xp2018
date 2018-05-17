package com.asso.conference.backgroundServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.asso.conference.db.BeaconQueue;

public class NetworkChangeReceiver extends BroadcastReceiver {

    BeaconQueue queue = BeaconQueue.INSTANCE;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable() || mobile.isAvailable()) {

            //TODO: send statistics
            //Use BeaconQueue
            Log.d("Network Available ", "Flag No 1");
        }
    }
}