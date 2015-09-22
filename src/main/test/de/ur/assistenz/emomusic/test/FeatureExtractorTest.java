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

    @Test
    public void testExtract() throws Exception {
        OverallAverageMFCC overallAverageMFCC = new OverallAverageMFCC(13, 100, 10000);
        OverallAverageRMS overallAverageRMS = new OverallAverageRMS();
        featureExtractor.addFeature(overallAverageMFCC);
        featureExtractor.addFeature(overallAverageRMS);
        featureExtractor.extract(new File("Alice Cooper - Poison.mp3"), null);
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
        System.out.println("MFCC:\t" + Arrays.toString(mfcc));
        System.out.println("RMS:\t" + rms);
    }

    // Array test helpers

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