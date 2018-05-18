package com.asso.conference.backgroundServices;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.asso.conference.bluetooth.BluetoothDevice;
import com.asso.conference.db.BeaconQueue;
import com.asso.conference.webClient.BookmarkCallback;
import com.asso.conference.webClient.UserService;
import com.asso.conference.webClient.models.BluetoothDeviceModel;

import java.io.IOException;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public class BluetoothService extends Service {

    //TODO: when beacon found add to the queue
    BeaconQueue beaconQueue = BeaconQueue.INSTANCE;
    UserService userService = UserService.INSTANCE;

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;

    private ObservableEmitter<HashMap<String,BluetoothDevice>> devicesObserver;
    private Observable<HashMap<String,BluetoothDevice>> devicesObservable;
    HashMap<String,BluetoothDevice> devices = new HashMap<String,BluetoothDevice>();


    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    private IBinder binder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("BluetoothService","Service bound");
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Bluetooth service starting", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        userService.getBluetoothDevices(new BookmarkCallback<BluetoothDeviceModel[]>() {
            @Override
            public void onSuccess(BluetoothDeviceModel[] responseDevices) {
                // TODO change
                for(int i=0; i<responseDevices.length;i++){
                    BluetoothDevice btTemp = new BluetoothDevice(responseDevices[i]);
                    devices.put(btTemp.getAddress(),btTemp);
                }



                btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
                btAdapter = btManager.getAdapter();
                btScanner = btAdapter.getBluetoothLeScanner();

                // Register the BroadcastReceiver
                IntentFilter filter = new IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

                IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                registerReceiver(mReceiver, filter2); // Don't forget to unregister during onDestroy

                // Starts scanning new devices
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        btAdapter.startDiscovery();
                        btScanner.startScan(leScanCallback);
                    }
                });
            }

            @Override
            public void onError(String message) {
                // TODO change
            }
        });


    }

    public Observable<HashMap<String,BluetoothDevice>> observeDevices() {
        if(devicesObservable == null) {
            devicesObservable = Observable.create(emitter -> devicesObserver = emitter);
            devicesObservable = devicesObservable.share();
        }
        return devicesObservable;
    }

    // Create a BroadcastReceiver for ACTION_FOUND and  ACTION_DISCOVERY_FINISHED
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (android.bluetooth.BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                android.bluetooth.BluetoothDevice device = intent.getParcelableExtra(android.bluetooth.BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getShortExtra(android.bluetooth.BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                // Add the name and address to the HashMap
                BluetoothDevice lastDevice = devices.get(device.getAddress());
                if(lastDevice != null) {
                    if(devicesObserver != null) {
                        lastDevice.setRssi(rssi);
                        lastDevice.setLastSignal(System.currentTimeMillis());
                        devices.put(device.getAddress(), lastDevice);
                        devicesObserver.onNext(devices);
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //try {
                    //Thread.sleep(15000);
                    btAdapter.startDiscovery();
                //} catch (InterruptedException e) {
                   // e.printStackTrace();
                //}
            }
        }
    };


    @Override
    public void onDestroy() {
        if(btScanner != null)
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    btScanner.stopScan(leScanCallback);
                    btAdapter.cancelDiscovery();
                }
            });
    }

    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d(">>>>>>>>>>>>>>>>>>",result.getDevice().getAddress());
            BluetoothDevice lastDevice = devices.get(result.getDevice().getAddress());
            if(lastDevice != null) {
                if(devicesObserver != null) {
                    Log.d("<<<<<<<<<<<<<<<<<<<<<",result.getDevice().getAddress());
                    lastDevice.setRssi(result.getRssi());
                    lastDevice.setLastSignal(System.currentTimeMillis());
                    devices.put(result.getDevice().getAddress(), lastDevice);
                    devicesObserver.onNext(devices);
                }
            }
        }
    };

}
