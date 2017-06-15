/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cortrium.cortriumc3;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cortrium.cortriumc3.APIConnection.CortriumAPI;
import com.cortrium.cortriumc3.APIConnection.ECGRecording;
import com.cortrium.cortriumc3.APIConnection.ECGRecordingDeserializer;
import com.cortrium.opkit.ConnectionManager;
import com.cortrium.opkit.CortriumC3;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity {
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private final static String TAG = "CortriumC3Ecg";

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private ConnectionManager connectionManager;
    private Context mContext = this;
    private String paired_id;

    private CortriumAPI cortriumAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private static final int REQUEST_ENABLE_BT = 1;

    private final ConnectionManager.OnConnectionManagerListener mListener = new ConnectionManager.OnConnectionManagerListener() {
        @Override
        public void startedScanning(ConnectionManager manager) {
        }

        @Override
        public void stoppedScanning(ConnectionManager manager) {

        }

        @Override
        public void discoveredDevice(final CortriumC3 device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });

        }

        @Override
        public void connectedToDevice(CortriumC3 device) {
        }

        @Override
        public void disconnectedFromDevice(CortriumC3 device) {

        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        //setButtonText("Bluetooth on");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.M)
    private void askForLocationPermissions()
    {
        // Android M Permission check
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect Cortrium devices");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });

            builder.show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Enable this for production
        //Fabric.with(this, new Crashlytics());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForLocationPermissions();
        }

        createCortriumAPI();
    }

    private void createCortriumAPI() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapter(ECGRecording.class, new ECGRecordingDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CortriumAPI.ENDPOINT)
                //.client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        cortriumAPI = retrofit.create(CortriumAPI.class);

        compositeDisposable.add(cortriumAPI.getRecording("d3cd1bd0-45f8-11e7-bfcf-3f8f418d896b")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getRecordingObserver()));
    }

    private DisposableSingleObserver<List<ECGRecording>> getRecordingsObserver() {
        return new DisposableSingleObserver<List<ECGRecording>>() {
            @Override
            public void onSuccess(List<ECGRecording> value) {
                Log.d(TAG, "onSuccess: "+value.size()+" values");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError");
                e.printStackTrace();
                Toast.makeText(DeviceScanActivity.this, "Can not load recordings", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private DisposableSingleObserver<ECGRecording> getRecordingObserver() {
        return new DisposableSingleObserver<ECGRecording>() {

            @Override
            public void onSuccess(ECGRecording value) {
                Log.d(TAG, "onSuccess: "+value.toString());
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError");
                e.printStackTrace();
            }
        };
    }

    @Override
    public void onStart(){
        super.onStart();

        //recover paired id
        paired_id = Utils.getPairedDevice(this);

        connectionManager = ConnectionManager.getInstance(this);
        connectionManager.setConnectionManagerListener(mListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to Cortrium devices when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (connectionManager.getConnectionState() == ConnectionManager.ConnectionStates.Scanning) {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                //mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        // UPDATE: The intent request was causing a timimg error, now is enabled directly from adapter.
        if (!connectionManager.getBluetoothIsEnabled()) {
            /*Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);*/
            BluetoothAdapter.getDefaultAdapter().enable();
        }

        if (!connectionManager.isCortriumDeviceSupported()) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error bluetooth not supported");
            finish();
        } else {
            mLeDeviceListAdapter = new LeDeviceListAdapter();
            setListAdapter(mLeDeviceListAdapter);
            scanLeDevice(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        if(mLeDeviceListAdapter != null)
            mLeDeviceListAdapter.clear();
        connectionManager.clear();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final CortriumC3 device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;

        if (connectionManager.getConnectionState() == ConnectionManager.ConnectionStates.Scanning) {
            scanLeDevice(false);
        }

        Log.d(TAG,"Device selected: "+device.getName());
        Utils.setPairedDevice(this, device.getName());

        connectToDevice(device);

    }

    private void connectToDevice(CortriumC3 device){
        connectionManager.connectDevice(device);

        Intent intent = new Intent(mContext, CortriumC3Ecg.class);
        startActivity(intent);
    }

    private void scanLeDevice(final boolean enable) {
        if(BluetoothAdapter.getDefaultAdapter() != null && BluetoothAdapter.getDefaultAdapter().isEnabled()){
            if (enable) {
                Log.d(TAG, "Start scanning");
                connectionManager.startScanning();
            } else {
                Log.d(TAG, "Stop scanning");
                connectionManager.stopScanning();
            }
        }
        invalidateOptionsMenu();
    }

    /**
     *  Adapter for holding devices found through scanning.
      */

    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<CortriumC3> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(CortriumC3 device) {
            if (!mLeDevices.contains(device)) {
                if(paired_id.compareTo(device.getName()) == 0)
                    connectToDevice(device);
                else{
                    mLeDevices.add(device);
                }
            }
        }

        public CortriumC3 getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            CortriumC3 device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}