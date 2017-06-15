package com.cortrium.cortriumc3.test.APIConnection;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kike Bodi on 06/06/2017.
 * Cortrium
 * bodi.inf@gmail.com
 */

public class ECGRecording {

    @SerializedName("filename") String filename;
    @SerializedName("id") String id;
    @SerializedName("start") String start;
    @SerializedName("url") String url;

    @Override
    public String toString() {
        return(filename+" : "+url);
    }
}
