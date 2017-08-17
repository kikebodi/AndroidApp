package com.cortrium.cortriumc3;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.cortrium.cortriumc3.ApiConnection.models.Channels;
import com.cortrium.cortriumc3.ApiConnection.models.Device;
import com.cortrium.cortriumc3.ApiConnection.models.Recordings;

/**
 * Created by Kike on 17/05/2017.
 */

public class Utils {

    private final static String TAG = "Utils";
    private static final String SHARED_PREFERENCES_KEY = TAG;
    private static final String PAIRED_DEVICE = "paired_device_id";

    public static void setPairedDevice(Context con, String deviceName){
        SharedPreferences mPrefs = con.getSharedPreferences(SHARED_PREFERENCES_KEY, 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString(PAIRED_DEVICE, deviceName).apply();
    }

    public static String getPairedDevice(Context con) {
        SharedPreferences mPrefs = con.getSharedPreferences(SHARED_PREFERENCES_KEY, 0);
        return mPrefs.getString(PAIRED_DEVICE, "");
    }

    public static Recordings generateRecordings(String deviceName, String firmwareVersion, String hardwareVersion, String fileName, int totalTime){
        Channels mChannels = new Channels(true,true,true,true);
        Device myDevice = new Device(deviceName, firmwareVersion, hardwareVersion,null,null,null);
        Recordings ret = new Recordings(null,fileName, totalTime, myDevice, mChannels, null, null);
        return ret;
    }
}
