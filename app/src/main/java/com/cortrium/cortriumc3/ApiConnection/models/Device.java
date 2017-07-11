
package com.cortrium.cortriumc3.ApiConnection.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Device {

    @SerializedName("identifier")
    @Expose
    private String identifier;
    @SerializedName("firmwareversion")
    @Expose
    private String firmwareversion;
    @SerializedName("hardwareversion")
    @Expose
    private String hardwareversion;
    @SerializedName("gain")
    @Expose
    private Integer gain;
    @SerializedName("samplingrate")
    @Expose
    private Integer samplingrate;
    @SerializedName("id")
    @Expose
    private String id;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Device() {
    }

    /**
     * 
     * @param id
     * @param firmwareversion
     * @param samplingrate
     * @param hardwareversion
     * @param identifier
     * @param gain
     */
    public Device(String identifier, String firmwareversion, String hardwareversion, Integer gain, Integer samplingrate, String id) {
        super();
        this.identifier = identifier;
        this.firmwareversion = firmwareversion;
        this.hardwareversion = hardwareversion;
        this.gain = gain;
        this.samplingrate = samplingrate;
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Device withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public String getFirmwareversion() {
        return firmwareversion;
    }

    public void setFirmwareversion(String firmwareversion) {
        this.firmwareversion = firmwareversion;
    }

    public Device withFirmwareversion(String firmwareversion) {
        this.firmwareversion = firmwareversion;
        return this;
    }

    public String getHardwareversion() {
        return hardwareversion;
    }

    public void setHardwareversion(String hardwareversion) {
        this.hardwareversion = hardwareversion;
    }

    public Device withHardwareversion(String hardwareversion) {
        this.hardwareversion = hardwareversion;
        return this;
    }

    public Integer getGain() {
        return gain;
    }

    public void setGain(Integer gain) {
        this.gain = gain;
    }

    public Device withGain(Integer gain) {
        this.gain = gain;
        return this;
    }

    public Integer getSamplingrate() {
        return samplingrate;
    }

    public void setSamplingrate(Integer samplingrate) {
        this.samplingrate = samplingrate;
    }

    public Device withSamplingrate(Integer samplingrate) {
        this.samplingrate = samplingrate;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Device withId(String id) {
        this.id = id;
        return this;
    }

}
