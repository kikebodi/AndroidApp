package com.docobo.contriumc3.APIConnection;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.cortrium.cortriumc3.ApiConnection.ApiConnectionManager;
import com.cortrium.cortriumc3.ApiConnection.models.Channels;
import com.cortrium.cortriumc3.ApiConnection.models.Device;
import com.cortrium.cortriumc3.ApiConnection.models.Recordings;
import com.cortrium.cortriumc3.CortriumC3Ecg;
import com.cortrium.cortriumc3.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kike Bodi on 10/07/2017.
 * Cortrium
 * bodi.inf@gmail.com
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ApiTest {

    private Context instrumentationContext;
    final CountDownLatch signal = new CountDownLatch(1);

    @Before
    public void setUp(){
        instrumentationContext = InstrumentationRegistry.getContext();
    }



    @Test
    public void testGET() throws InterruptedException {
        String url = InstrumentationRegistry.getTargetContext().getString(R.string.api_url);
        ApiConnectionManager apiManager = new ApiConnectionManager(url);
        apiManager.getRecordingFromAPI("5bf2eb60-655d-11e7-8c8c-d5743dc97a59");

        signal.await(3, TimeUnit.SECONDS);
        Assert.assertTrue(ApiConnectionManager.successfullGetRequest);
    }

    public void testGET(String id) throws InterruptedException {
        String url = InstrumentationRegistry.getTargetContext().getString(R.string.api_url);
        ApiConnectionManager apiManager = new ApiConnectionManager(url);
        apiManager.getRecordingFromAPI(id);

        //signal.await(3, TimeUnit.SECONDS);
        //Assert.assertTrue(ApiConnectionManager.successfullGetRequest);
    }

    @Test
    public void testPOST() throws InterruptedException {
        String recordingName = "recording_test"+System.currentTimeMillis();
        String url = InstrumentationRegistry.getTargetContext().getString(R.string.api_url);
        ApiConnectionManager connector = new ApiConnectionManager(url);

        File mFile = createFile(recordingName);
        Recordings recording = createRecording(recordingName);
        connector.postRecordingToAPI(recording,mFile);

        signal.await(10, TimeUnit.SECONDS);
        Assert.assertTrue(ApiConnectionManager.successfullPostRequest);
    }

    @Test
    public void testDELETE() throws InterruptedException {
        String url = InstrumentationRegistry.getTargetContext().getString(R.string.api_url);
        ApiConnectionManager connector = new ApiConnectionManager(url);
        connector.deleteRecording(ApiConnectionManager.lastRecordingId);
        //connector.deleteRecording("0a9dc650-6584-11e7-8c8c-d5743dc97a59");
        signal.await(3, TimeUnit.SECONDS);
        testGET(ApiConnectionManager.lastRecordingId);
        signal.await(3, TimeUnit.SECONDS);
        Assert.assertTrue(!ApiConnectionManager.successfullGetRequest);
    }

    private Recordings createRecording(String filename){
        Channels myChannels = new Channels(true, true, true, true);
        Device myDevice = new Device("C3-RetrofitTest",null,null,null,null,null);
        Recordings myRecording = new Recordings(null,filename,10,myDevice,myChannels,null,null);

        return myRecording;
    }

    private File createFile(String filename){

        File file = new File(instrumentationContext.getExternalFilesDir(null), filename);
        String path = instrumentationContext.getExternalFilesDir(null)+"/"+filename;
        try {
            FileOutputStream outputStream = instrumentationContext.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write("hjghjghghg".getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}
