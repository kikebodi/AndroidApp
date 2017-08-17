package com.docobo.contriumc3.APIConnection;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.cortrium.cortriumc3.ApiConnection.ApiConnectionManager;
import com.cortrium.cortriumc3.ApiConnection.models.Channels;
import com.cortrium.cortriumc3.ApiConnection.models.Device;
import com.cortrium.cortriumc3.ApiConnection.models.Recordings;
import com.cortrium.cortriumc3.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
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
    }

    @Test
    public void testPOST() throws InterruptedException {
        String recordingName = "recording_test"+System.currentTimeMillis();
        String url = InstrumentationRegistry.getTargetContext().getString(R.string.api_url);
        ApiConnectionManager connector = new ApiConnectionManager(url);

        File mFile = getFile();
        Recordings recording = createRecording(mFile.getName());
        connector.postRecordingToAPI(recording,mFile);

        signal.await(10, TimeUnit.SECONDS);
        Assert.assertTrue(ApiConnectionManager.successfullPostRequest);
        Assert.assertTrue(ApiConnectionManager.successfullUploadRequest);
    }

    @Test
    public void testDELETE() throws InterruptedException {
        String url = InstrumentationRegistry.getTargetContext().getString(R.string.api_url);
        ApiConnectionManager connector = new ApiConnectionManager(url);
        connector.deleteRecording(ApiConnectionManager.lastRecordingId);
        //connector.deleteRecording("0a9dc650-6584-11e7-8c8c-d5743dc97a59");
        signal.await(3, TimeUnit.SECONDS);
        //It always return true so we need to do a GET request to check it.
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

    public File getFile(){
        //Change for a file in your phone. Check also the path
        File bleFile = new File(InstrumentationRegistry.getTargetContext().getExternalFilesDir(null),"CortriumC3Data/2017-05-19-12-36-24-C3-849C7913F5D4/591EE6C8.BLE");
        return bleFile;
    }
}
