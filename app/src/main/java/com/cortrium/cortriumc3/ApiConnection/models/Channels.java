
package com.cortrium.cortriumc3.ApiConnection.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Channels {

    @SerializedName("ecg1")
    @Expose
    private Boolean ecg1;
    @SerializedName("ecg2")
    @Expose
    private Boolean ecg2;
    @SerializedName("ecg3")
    @Expose
    private Boolean ecg3;
    @SerializedName("resp")
    @Expose
    private Boolean resp;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Channels() {
    }

    /**
     *
     * @param ecg2
     * @param ecg1
     * @param ecg3
     * @param resp
     */
    public Channels(Boolean ecg1, Boolean ecg2, Boolean ecg3, Boolean resp) {
        super();
        this.ecg1 = ecg1;
        this.ecg2 = ecg2;
        this.ecg3 = ecg3;
        this.resp = resp;
    }

    public Boolean getEcg1() {
        return ecg1;
    }

    public void setEcg1(Boolean ecg1) {
        this.ecg1 = ecg1;
    }

    public Channels withEcg1(Boolean ecg1) {
        this.ecg1 = ecg1;
        return this;
    }

    public Boolean getEcg2() {
        return ecg2;
    }

    public void setEcg2(Boolean ecg2) {
        this.ecg2 = ecg2;
    }

    public Channels withEcg2(Boolean ecg2) {
        this.ecg2 = ecg2;
        return this;
    }

    public Boolean getEcg3() {
        return ecg3;
    }

    public void setEcg3(Boolean ecg3) {
        this.ecg3 = ecg3;
    }

    public Channels withEcg3(Boolean ecg3) {
        this.ecg3 = ecg3;
        return this;
    }

    public Boolean getResp() {
        return resp;
    }

    public void setResp(Boolean resp) {
        this.resp = resp;
    }

    public Channels withResp(Boolean resp) {
        this.resp = resp;
        return this;
    }

}
