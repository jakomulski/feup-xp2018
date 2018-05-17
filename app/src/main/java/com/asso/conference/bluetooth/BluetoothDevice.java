package com.asso.conference.bluetooth;

public class BluetoothDevice {
    private String address;
    private int roomId;
    private int rssi;
    private long lastSignal;
    private float x;
    private float y;

    public BluetoothDevice(String address, int roomId, int rssi, long lastSignal, float x, float y){
        this.address = address;
        this.roomId = roomId;
        this.rssi = rssi;
        this.lastSignal = lastSignal;
        this.x = x;
        this.y = y;
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
