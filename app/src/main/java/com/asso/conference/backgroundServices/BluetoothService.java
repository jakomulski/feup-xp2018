package com.asso.conference.backgroundServices;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.asso.conference.bluetooth.BluetoothDevice;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public class BluetoothService extends Service {

    //TODO: when beacon found add to the queue
    BeaconQueue beaconQueue = BeaconQueue.INSTANCE;

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

        // TODO change hardcoded bluetooth devices
        BluetoothDevice midi = new BluetoothDevice("C4:BE:84:49:DD:7E", 1, 1000, 0);
        BluetoothDevice sensorTag = new BluetoothDevice("B0:B4:48:BC:E5:82", 2,1000, 0);

        devices.put("C4:BE:84:49:DD:7E", midi);
        devices.put("B0:B4:48:BC:E5:82", sensorTag);


        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        // Starts scanning new devices
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
                btAdapter.startDiscovery();
            }
        });


        // Register the BroadcastReceiver
        //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
       // registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

    }

    public Observable<HashMap<String,BluetoothDevice>> observeDevices() {
        if(devicesObservable == null) {
            devicesObservable = Observable.create(emitter -> devicesObserver = emitter);
            devicesObservable = devicesObservable.share();
        }
        return devicesObservable;
    }


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
            BluetoothDevice lastDevice = devices.get(result.getDevice().getAddress());
            if(lastDevice != null) {
                if(devicesObserver != null) {
                    lastDevice.setRssi(result.getRssi());
                    lastDevice.setLastSignal(result.getTimestampNanos());
                    devices.put(result.getDevice().getAddress(), lastDevice);
                    devicesObserver.onNext(devices);
                }
            }
        }
    };

}
