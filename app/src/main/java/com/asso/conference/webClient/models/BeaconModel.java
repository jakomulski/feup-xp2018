package com.asso.conference.webClient.models;

public class BeaconModel {

    public String roomId;
    public int sessionId;
    public long time;

    public BeaconModel(String roomId, long time) {
        this.roomId = roomId;
        this.sessionId = 0;
        this.time = time;
    }

}
