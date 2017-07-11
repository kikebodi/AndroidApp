package com.cortrium.cortriumc3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;

import com.cortrium.cortriumc3.ApiConnection.ApiConnectionManager;
import com.cortrium.cortriumc3.ApiConnection.models.Channels;
import com.cortrium.cortriumc3.ApiConnection.models.Device;
import com.cortrium.cortriumc3.ApiConnection.models.Events;
import com.cortrium.cortriumc3.ApiConnection.models.Recordings;
import com.cortrium.opkit.ConnectionManager;
import com.cortrium.opkit.CortriumC3;
import com.cortrium.opkit.Utils;
import com.cortrium.opkit.datatypes.Event;
import com.cortrium.opkit.datatypes.SensorMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by hsk on 21/10/16.
 */

public final class DataLogger {

    private final static String TAG = "CortriumC3Comms";
    private final String FOLDER_NAME = "CortriumC3Data";
    private String recordingFilename;

    private RandomAccessFile mBleDataFile;
    private boolean newFile;
    private Context mContext;
    private CortriumC3 mDevice;
    private long currentFilePointer;
    private long startRecording;

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            if (ConnectionManager.ACTION_GATT_DISCONNECTED.equals(action))
            {
                closeDataWriters();
            }
            else if (ConnectionManager.ACTION_DEVICE_MODE_UPDATED.equals(action))
            {
                SensorMode sensorMode = intent.getParcelableExtra(ConnectionManager.EXTRA_VALUE);
                onModeUpdated(sensorMode);
            }
        }
    };

    public DataLogger(Context context, CortriumC3 device)
    {
        mContext = context;
        mContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        mDevice = device;
        currentFilePointer = 0;
        newFile = true;
        startRecording = System.currentTimeMillis();
    }

    public void unregisterReceiver(){
        mContext.unregisterReceiver(mGattUpdateReceiver);
    }

    private IntentFilter makeGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectionManager.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(ConnectionManager.ACTION_DEVICE_MODE_UPDATED);
        return intentFilter;
    }

    private void onModeUpdated(SensorMode sensorMode)
    {
        if (sensorMode.getMode() == SensorMode.MODE_IDLE)
        {
            closeDataWriters();
        }
        else if (sensorMode.getMode() == SensorMode.MODE_ACTIVE || sensorMode.getMode() == SensorMode.MODE_HOLTER)
        {
            String filename = sensorMode.getFileName();
            recordingFilename = filename;

            if (bleFileExists(filename))
            {
                Log.d(TAG, String.format("BLE file %s already exists", filename));
                newFile = false;
                openExistingFileDumpStream(filename);
            }
            else
            {
                Log.d(TAG, String.format("BLE file %s doesn't exist...creating it", filename));
                newFile = true;
                initialiseFileDumpStream(filename);
            }
            enableUpload(filename);
        }
    }

    public void logBlePayload(ByteBuffer rawBlePayload, long serial)
    {
        if (mBleDataFile != null)
        {
            try
            {
                int bufferSize = rawBlePayload.capacity();
                long fileWriteLocation = serial * bufferSize;

                if (newFile) {
                    if (fileWriteLocation > currentFilePointer + bufferSize) {
                        // With new files we have to fill the gap from serial 0 -> serial * bufferSize with 0's
                        byte[] bytes = new byte[(int)(fileWriteLocation - currentFilePointer)];
                        mBleDataFile.write(bytes);
                    }
                }
                else {
                    long fileSize = mBleDataFile.length();
                    if (fileSize < fileWriteLocation) {
                        // With existing files we have to fill the gap from mBleDataFile.length -> serial * bufferSize with 0's
                        byte[] bytes = new byte[(int)(fileWriteLocation - fileSize)];
                        mBleDataFile.write(bytes);
                    }
                }

                //Log.v(TAG, String.format("Writing serial %d at offset %d", serial, serial * bufferSize));
                mBleDataFile.seek(serial * bufferSize);
                mBleDataFile.write(rawBlePayload.array());

                currentFilePointer = mBleDataFile.getFilePointer();
            }
            catch (Exception ex)
            {
                Log.e(TAG, String.format("Error seekting to specific offset: %d", serial), ex);
            }
        }
    }

    private boolean bleFileExists(String filename)
    {
        String foldername = folderForFilename(filename);

        return new File(foldername, filename).exists();
    }

    private void openExistingFileDumpStream(String filename) {
        String foldername = folderForFilename(filename);

        try {
            File outputFile = new File(foldername, filename);
            mBleDataFile = new RandomAccessFile(outputFile, "rws");
        }
        catch (FileNotFoundException ex) {
            Log.e(TAG, String.format("Error opening BLE file %s", filename), ex);
        }
    }

    private void initialiseFileDumpStream(String filename) {

        // If there are data writers open close them.
        closeDataWriters();

        try
        {
            String folderNameWithDeviceId = folderForFilename(filename);
            File fileDumpParentFolder = new File(mContext.getExternalFilesDir(null), FOLDER_NAME);
            File fileDumpFolder = new File(fileDumpParentFolder, folderNameWithDeviceId);
            if (!fileDumpFolder.exists()){
                fileDumpFolder.mkdirs();
                if (!fileDumpFolder.exists()){
                    Log.d(TAG, "The could not create the folder");
                }
            }
            File outputFile = new File(fileDumpFolder, filename);
            mBleDataFile = new RandomAccessFile(outputFile, "rws");
        }
        catch(Exception e)
        {
            Log.e(TAG, "Failed to create file dump streams", e);
        }
    }

    private String folderForFilename(String filename) {
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.ENGLISH);
        timestampFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String folderNameFormat = timestampFormat.format(Utils.dateFromFilename(filename));
        return String.format("%s %s", folderNameFormat, mDevice.getName()).replace(' ','-').replace('.','-');
    }

    private File getCurrentRecordingFile(){
        File bleFile = new File(mContext.getExternalFilesDir(null)+File.separator+FOLDER_NAME+File.separator+folderForFilename(recordingFilename), recordingFilename);
        File bleFile_copy = new File(mContext.getExternalFilesDir(null)+File.separator+FOLDER_NAME+File.separator+folderForFilename(recordingFilename), recordingFilename.replace(".BLE","")+"_copy.BLE");
        try {
            copy(bleFile,bleFile_copy);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bleFile_copy.exists() ? bleFile_copy : null;
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    private void closeDataWriters()
    {
        if (mBleDataFile!= null)
        {
            try {
                mBleDataFile.close();
            }
            catch (Exception e)
            {
                Log.e(TAG, "Error occurred closing BLE file", e);
            }
        }

        mBleDataFile = null;
        currentFilePointer = 0;
    }

    private void enableUpload(String filename){
        if(mContext == null || ((CortriumC3Ecg)mContext).main_fragment == null) return;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Event> events = ((CortriumC3Ecg)mContext).main_fragment.getEventList();
                ((CortriumC3Ecg)mContext).main_fragment.fab.hide();

                Channels mChannels = new Channels(true,true,true,true);
                Device myDevice = new Device(mDevice.getName(),mDevice.getFirmwareRevision(),mDevice.getHardwareRevision(),null,null,null);
                Integer totalTime = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()-startRecording);
                Recordings myRecording = new Recordings(null,recordingFilename, totalTime, myDevice, mChannels, null, null);
                ApiConnectionManager connector = new ApiConnectionManager(mContext.getResources().getString(R.string.api_url));
                File bleFile = getCurrentRecordingFile();
                connector.postRecordingToAPI(myRecording,bleFile);
            }
        };
        ((CortriumC3Ecg)mContext).main_fragment.setSnackbar(filename,listener);
    }
}
