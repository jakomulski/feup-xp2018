package com.asso.conference.bluetooth;

public class BluetoothDevice {
    private String address;
    private int roomId;
    private int rssi;
    private long lastSignal;

    public BluetoothDevice(String address, int roomId, int rssi, long lastSignal){
        this.address = address;
        this.roomId = roomId;
        this.rssi = rssi;
        this.lastSignal = lastSignal;
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

}
