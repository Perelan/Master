package com.cesar.moduletemplate.Payload;

import java.util.Date;

public class Record {

    private int id;

    private String name, description;
    private long avgSampleRate, monitorTime;
    private float rating;
    private Date createdAt, updatedAt;
    private User user;

    private int nrSamples;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getAvgSampleRate() {
        return avgSampleRate;
    }

    public long getMonitorTime() {
        return monitorTime;
    }

    public float getRating() {
        return rating;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public User getUser() {
        return user;
    }

    public int getNrSamples() {
        return nrSamples;
    }
}
