package com.cortrium.cortriumc3.test;

import com.cortrium.opkit.MovingAverage;
import com.cortrium.opkit.PeakDetector;
import com.cortrium.opkit.Utils;
import com.cortrium.opkit.filters.EcgHighPassFilter;
import com.cortrium.opkit.filters.EcgLowPassFilter;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by shavinda.rabichandra on 04/08/2016.
 */
public class ECGFiltersTest
{
    @Test
    public void testEcgHighPassFilter()
    {
        // Setup
        int[] testSamples = new int[] {353140736, 352894976, 352896000, 353144320, 353277696, 353156608, 352918784, 352901376, 353132544, 353262848, 353139968, 352904448};
        int[] resultSamples = new int[12];

        EcgHighPassFilter filter = new EcgHighPassFilter();
        for (int index = 0; index < testSamples.length; index++) {
            resultSamples[index] = filter.filterInput(testSamples[index]);
        }

        // Validate
        int[] expectedResult = new int[] {-32766, 32272, 32020, 32013, 31893, 31525, 31047, 30787, 30773, 30659, 30300, 29833};
        for (int index = 0; index < testSamples.length; index++) {
            Assert.assertEquals(expectedResult[index], resultSamples[index]);
        }
    }

    @Test
    public void testEcgLowPassFilter()
    {
        // Setup
        short[] testSamples = new short[] {15165, 15046, 22252, 22078, 15014, 14897, 22083, 21910, 15290, 15170, 15052, 21933};
        short[] resultSamples = new short[12];

        EcgLowPassFilter filter = new EcgLowPassFilter();
        for (int index = 0; index < testSamples.length; index++) {
            resultSamples[index] = filter.filterInput(testSamples[index]);
        }

        // Validate
        short[] expectedResult = new short[] {2, 33, 39, 49, 39, 17, 16, 43, 59, 46, 23, 4};
        for (int index = 0; index < testSamples.length; index++) {
            Assert.assertEquals(expectedResult[index], resultSamples[index]);
        }
    }

    @Test
    public void testMovingAverage()
    {
        MovingAverage movingAverage = new MovingAverage(5);

        movingAverage.addSample(1);
        movingAverage.addSample(2);
        movingAverage.addSample(3);
        movingAverage.addSample(4);
        movingAverage.addSample(5);
        movingAverage.addSample(6);
        movingAverage.addSample(7);
        movingAverage.addSample(8);
        movingAverage.addSample(9);
        movingAverage.addSample(10);

        Assert.assertEquals(movingAverage.getMovingAverage(), 8);
    }

    @Test
    public void testPeakDetection()
    {
        float[] samples = { 10, 20, 30, 40, 50, 40, 30, 20, 10, 20, 30, 40, 50, 100, 200, 300, 500, 700, 900, 1200, 900, 700, 500, 300, 200, 100, 0};
        PeakDetector peakDetector = new PeakDetector();

        for (float sample: samples)
        {
            peakDetector.detectPeak(sample);
        }

        Assert.assertEquals(peakDetector.numberOfEmiPeaks, 1);
    }

    @Test
    public void testValidateSampleConverter() {
        byte[] ecg1 = {52, 29, 96};
        byte[] ecg2 = {-1, -1, 127};
        byte[] ecg3 = {-1, -1, 127};

        int convertedEcg1 = Utils.convertSampleValue(ecg1);
        int convertedEcg2 = Utils.convertSampleValue(ecg2);
        int convertedEcg3 = Utils.convertSampleValue(ecg3);

        /*Assert.assertEquals(convertedEcg1, 6298932);
        Assert.assertEquals(convertedEcg2, 8388607);
        Assert.assertEquals(convertedEcg3, 8388607);*/
    }

    @Test
    public void testValidateSampleConverter2() {
        byte[] ecg1 = { 101, 69, -2};

        int convertedEcg1 = Utils.convertSampleValue(ecg1);

        Assert.assertEquals(convertedEcg1, -29006592);
    }

    @Test
    public void testBeatDetectionAndClassification(){

    }
}
