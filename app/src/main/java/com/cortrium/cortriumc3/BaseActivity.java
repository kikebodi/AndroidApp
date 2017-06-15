package com.cortrium.cortriumc3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class BaseActivity extends AppCompatActivity {

    private final static String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG,"onRestart");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG,"onResume");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG,"onPause");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    /**
     * Inner class for BroadcastReceiver. It's used in CortriumC3Ecg but not implemented yet.
     * We keep it here as security.
     */
    public static final class EventBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
        }
    }
}
