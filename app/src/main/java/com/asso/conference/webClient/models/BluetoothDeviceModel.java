package com.asso.conference.webClient.models;

public class BluetoothDeviceModel {
    public String address;
    public int roomId;
    public float x;
    public float y;

    public BluetoothDeviceModel(String address, int roomId , float x, float y){
        this.address = address;
        this.roomId = roomId;
        this.x = x;
        this.y = y;
    }
}
