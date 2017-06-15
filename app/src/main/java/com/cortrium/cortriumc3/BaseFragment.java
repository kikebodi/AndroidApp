package com.cortrium.cortriumc3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

/**
 * Created by Kike on 18/05/2017.
 */

public class BaseFragment extends Fragment {

    private final static String TAG = "BaseFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"onResume");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG,"onPause");
    }



    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(TAG,"onDestroyView");
    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

}
