package com.asso.conference.webClient.models;

public class EventModel {
    public String name;
    public String description;
    public int minsToHappen;

    public EventModel(String name, String description , int minsToHappen){
        this.name = name;
        this.description = description;
        this.minsToHappen = minsToHappen;
    }
}
