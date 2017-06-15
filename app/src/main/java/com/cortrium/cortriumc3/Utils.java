package com.cortrium.cortriumc3;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Kike on 17/05/2017.
 */

public class Utils {

    private final static String TAG = "CortriumC3Ecg";
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
}
