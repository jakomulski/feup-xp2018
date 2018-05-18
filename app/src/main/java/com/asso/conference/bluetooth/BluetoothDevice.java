package com.asso.conference.bluetooth;

import com.asso.conference.webClient.models.BluetoothDeviceModel;

public class BluetoothDevice {
    private String address;
    private int roomId;
    private int rssi;
    private long lastSignal;
    private float x;
    private float y;

    public BluetoothDevice(String address, int roomId , float x, float y){
        this.address = address;
        this.roomId = roomId;
        this.rssi = -100;
        this.lastSignal = 0;
        this.x = x;
        this.y = y;
    }

    public BluetoothDevice(BluetoothDeviceModel btModel){
        this.address = btModel.address;
        this.roomId = btModel.roomId;
        this.rssi = -100;
        this.lastSignal = 0;
        this.x = btModel.x;
        this.y = btModel.y;
    }

    public String getAddress() {
        return address;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public long getLastSignal() {
        return lastSignal;
    }

    public void setLastSignal(long lastSignal) {
        this.lastSignal = lastSignal;
    }



    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
