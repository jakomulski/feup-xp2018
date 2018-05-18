package com.asso.conference.backgroundServices;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.asso.conference.bluetooth.BluetoothDevice;
import com.asso.conference.db.BeaconQueue;
import com.asso.conference.webClient.BookmarkCallback;
import com.asso.conference.webClient.UserService;
import com.asso.conference.webClient.models.BeaconModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class BeaconQueuePushService extends IntentService{

    BeaconQueue beaconQueue = BeaconQueue.INSTANCE;
    UserService userService = UserService.INSTANCE;

    private boolean isSending = false;

    private int MILIS_BETWEEN_REQUEST = 30000;

    protected ServiceConnection serviceConnection;
    private BluetoothService service;
    private Disposable disposable;
    public HashMap<String, Long> lastSentSignals = null;


    public BeaconQueuePushService() {
        super("Collector");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Beacon Queue Push service starting", Toast.LENGTH_SHORT).show();

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d("Beacon Queue Push","BlueMAX service bound");
                service = ((BluetoothService.LocalBinder)iBinder).getService();
                disposable = service.observeDevices()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(devices -> {
                            updateBluetoothDevices(devices);
                        });
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d("Beacon Queue Push","Service disconnected");
            }
        };
        bindService(new Intent( this, BluetoothService.class), serviceConnection, BIND_AUTO_CREATE);
        return super.onStartCommand(intent,flags,startId);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    protected void updateBluetoothDevices(HashMap<String, BluetoothDevice> devices){
        if(lastSentSignals == null){ // first time called
            lastSentSignals = new HashMap<String, Long>();
            Iterator it = devices.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                BluetoothDevice btDevice = ((BluetoothDevice)pair.getValue());
                lastSentSignals.put(btDevice.getAddress(), btDevice.getLastSignal()); // populate lastSentBluetoothDevices
                if(btDevice.getLastSignal() > 0) {
                    BeaconModel beaconModel = new BeaconModel(btDevice.getRoomId(), btDevice.getLastSignal());
                    BeaconQueue.INSTANCE.push(beaconModel);
                }
            }
            Toast.makeText(this, "First added to queue", Toast.LENGTH_SHORT).show();
        } else {
            Iterator it = devices.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                BluetoothDevice btDevice = ((BluetoothDevice)pair.getValue());
                long lastSentSignal = lastSentSignals.get(btDevice.getAddress());
                if(btDevice.getLastSignal() > lastSentSignal + MILIS_BETWEEN_REQUEST) {
                    BeaconModel beaconModel = new BeaconModel(btDevice.getRoomId(), btDevice.getLastSignal());
                    BeaconQueue.INSTANCE.push(beaconModel);
                    lastSentSignals.put(btDevice.getAddress(),btDevice.getLastSignal());
                }
            }
        }
    }
}
