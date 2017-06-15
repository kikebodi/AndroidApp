package com.cortrium.cortriumc3.test;

import android.os.Environment;

import junit.framework.Assert;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.util.Arrays;

/**
 * Created by hsk on 01/11/16.
 */

public class DataLoggingTest{

    /**
     * Test for the Bug found in "private void initialiseFileDumpStream(String filename)" at DataLogger.java
     * Solution: In some devices the file system may be more restricted. That's why we should use getExternalFilesDir(null) [No user permission needed]
     */
    @Test
    public void testInitialiseFileDumpStream(){
        try
        {
            String folderNameWithDeviceId = "example-device-folder-1234";
            File fileDumpParentFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/CortriumC3Data");
            File fileDumpFolder = new File(fileDumpParentFolder, folderNameWithDeviceId);
            if (!fileDumpFolder.exists()){
                fileDumpFolder.mkdirs();
                if (!fileDumpFolder.exists()){
                    Assert.fail("The folder could not be created");
                }
            }
            File outputFile = new File(fileDumpFolder, Math.random()+".BLE");
            RandomAccessFile mBleDataFile = new RandomAccessFile(outputFile, "rws");
        }
        catch(Exception e)
        {
            //Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDataFormatInLogFile() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream stream = getClass().getResourceAsStream("581895D1.BLE");

        try {
            BufferedInputStream br = new BufferedInputStream(stream, 8000);

            byte[] readBuffer = new byte[80];
            while ((br.read(readBuffer, 0, readBuffer.length)) != -1) {
                ByteBuffer misc = ByteBuffer.wrap(readBuffer, 0, 20);
                misc.order(ByteOrder.LITTLE_ENDIAN);

                long serial = misc.getInt();

                int accX = misc.getShort();
                int accY = misc.getShort();
                int accZ = misc.getShort();

                int temp = misc.getShort();
                int serial_ADS = misc.getShort();

                byte batteryStatus = misc.get();
                byte resp1 = misc.get();
                byte resp2 = misc.get();
                byte resp3 = misc.get();

                byte leadoff = misc.get();
                byte conf = misc.get();


                if (serial != 0) {
                    ByteBuffer ecg1 = ByteBuffer.wrap(readBuffer, 20, 20);
                    ByteBuffer ecg2 = ByteBuffer.wrap(readBuffer, 40, 20);
                    ByteBuffer ecg3 = ByteBuffer.wrap(readBuffer, 60, 20);
                }
            }

            br.close();
        }
        catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }
}
