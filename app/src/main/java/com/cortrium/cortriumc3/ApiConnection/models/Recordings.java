
package com.cortrium.cortriumc3.ApiConnection.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Recordings {

    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("device")
    @Expose
    private Device device;
    @SerializedName("channels")
    @Expose
    private Channels channels;
    @SerializedName("events")
    @Expose
    private Events events;
    @SerializedName("url")
    @Expose
    private String url;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Recordings() {
    }

    /**
     *
     * @param duration
     * @param start
     * @param events
     * @param device
     * @param channels
     * @param filename
     * @param url
     */
    public Recordings(String start, String filename, Integer duration, Device device, Channels channels, Events events, String url) {
        super();
        this.start = start;
        this.filename = filename;
        this.duration = duration;
        this.device = device;
        this.channels = channels;
        this.events = events;
        this.url = url;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public Recordings withStart(String start) {
        this.start = start;
        return this;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Recordings withFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Recordings withDuration(Integer duration) {
        this.duration = duration;
        return this;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Recordings withDevice(Device device) {
        this.device = device;
        return this;
    }

    public Channels getChannels() {
        return channels;
    }

    public void setChannels(Channels channels) {
        this.channels = channels;
    }

    public Recordings withChannels(Channels channels) {
        this.channels = channels;
        return this;
    }

    public Events getEvents() {
        return events;
    }

    public void setEvents(Events events) {
        this.events = events;
    }

    public Recordings withEvents(Events events) {
        this.events = events;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Recordings withUrl(String url) {
        this.url = url;
        return this;
    }

}
