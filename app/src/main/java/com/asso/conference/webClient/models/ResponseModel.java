package com.asso.conference.webClient.models;

public class ResponseModel<T> {
    public T success;
    public BluetoothDeviceModel[] bluetoothDevices;
    public String failure;
}
