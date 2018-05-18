package com.asso.conference.webClient.models;

public class BeaconModel {

    public int roomId;
    public int sessionId;
    public long time;

    public BeaconModel(int roomId, long time) {
        this.roomId = roomId;
        this.sessionId = 0;
        this.time = time;
    }

}
