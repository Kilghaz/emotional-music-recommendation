package de.ur.assistenz.emomusic.test;

import de.ur.assistenz.emomusic.classifier.FeatureExtractor;
import de.ur.assistenz.emomusic.classifier.features.OverallAverageMFCC;
import de.ur.assistenz.emomusic.classifier.features.OverallAverageRMS;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

public class FeatureExtractorTest {

    FeatureExtractor featureExtractor = new FeatureExtractor(512, 0);
    float mfccFilterFrequencyLow = 100;
    float mfccFilterFrequencyHigh = 10000;

    @Test
    public void testExtractMP3() throws Exception {
        OverallAverageMFCC overallAverageMFCC = new OverallAverageMFCC(13, mfccFilterFrequencyLow, mfccFilterFrequencyHigh);
        OverallAverageRMS overallAverageRMS = new OverallAverageRMS();
        featureExtractor.addFeature(overallAverageMFCC);
        featureExtractor.addFeature(overallAverageRMS);
        featureExtractor.extract(new File("test-resources/test_audio.mp3"));
        float[] mfcc = overallAverageMFCC.getFeatureValue();
        float rms = overallAverageRMS.getOverallAverageRMS();
        Assert.assertNotNull(mfcc);
        assertArrayValuesNot(mfcc, 0);
        assertArrayValuesNot(mfcc, Float.POSITIVE_INFINITY);
        assertArrayValuesNot(mfcc, Float.NEGATIVE_INFINITY);
        assertArrayValuesNot(mfcc, Float.NaN);
        Assert.assertNotEquals(rms, Float.POSITIVE_INFINITY);
        Assert.assertNotEquals(rms, Float.NEGATIVE_INFINITY);
        Assert.assertNotEquals(rms, Float.NaN);
        System.err.println("MFCC:\t" + Arrays.toString(mfcc));
        System.err.println("RMS:\t" + rms);
    }

    @Test
    public void testExtractWAV() throws Exception {
        OverallAverageMFCC overallAverageMFCC = new OverallAverageMFCC(13, mfccFilterFrequencyLow, mfccFilterFrequencyHigh);
        OverallAverageRMS overallAverageRMS = new OverallAverageRMS();
        featureExtractor.addFeature(overallAverageMFCC);
        featureExtractor.addFeature(overallAverageRMS);
        featureExtractor.extract(new File("test-resources/test_audio.wav"));
        float[] mfcc = overallAverageMFCC.getFeatureValue();
        float rms = overallAverageRMS.getOverallAverageRMS();
        Assert.assertNotNull(mfcc);
        assertArrayValuesNot(mfcc, 0);
        assertArrayValuesNot(mfcc, Float.POSITIVE_INFINITY);
        assertArrayValuesNot(mfcc, Float.NEGATIVE_INFINITY);
        assertArrayValuesNot(mfcc, Float.NaN);
        Assert.assertNotEquals(rms, Float.POSITIVE_INFINITY);
        Assert.assertNotEquals(rms, Float.NEGATIVE_INFINITY);
        Assert.assertNotEquals(rms, Float.NaN);
        System.err.println("MFCC:\t" + Arrays.toString(mfcc));
        System.err.println("RMS:\t" + rms);
    }

    private void assertArrayValuesNot(float[] array, float value) {
        for (float val : array) {
            Assert.assertNotEquals(val, value);
        }
    }

    private void assertArrayValuesNotNull(float[] array) {
        for (float val : array) {
            Assert.assertNotNull(val);
        }
    }

}