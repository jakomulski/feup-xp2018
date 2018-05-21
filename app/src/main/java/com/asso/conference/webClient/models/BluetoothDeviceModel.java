package com.asso.conference.webClient.models;

public class BluetoothDeviceModel {
    public String address;
    public String roomId;
    public float x;
    public float y;

    public BluetoothDeviceModel(String address, String roomId , float x, float y){
        this.address = address;
        this.roomId = roomId;
        this.x = x;
        this.y = y;
    }
}
