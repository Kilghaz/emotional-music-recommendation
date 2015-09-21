package de.ur.assistenz.emomusic.test;

import de.ur.assistenz.emomusic.classifier.FeatureExtractor;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

public class FeatureExtractorTest {

    FeatureExtractor featureExtractor = new FeatureExtractor(512, 0);

    @Test
    public void testExtract() throws Exception {
        featureExtractor.extract(new File("Alice Cooper - Poison.mp3"));
        float[] mfcc = featureExtractor.getOverallAverageMFCC();
        double rms = featureExtractor.getOverallAverageRMS();
        Assert.assertNotNull(mfcc);
        assertArrayValuesNot(mfcc, 0);
        assertArrayValuesNot(mfcc, Float.POSITIVE_INFINITY);
        assertArrayValuesNot(mfcc, Float.NEGATIVE_INFINITY);
        assertArrayValuesNot(mfcc, Float.NaN);
        assertArrayValuesNotNull(mfcc);
        Assert.assertNotEquals(rms, Double.POSITIVE_INFINITY);
        Assert.assertNotEquals(rms, Double.NEGATIVE_INFINITY);
        Assert.assertNotEquals(rms, Double.NaN);
        System.out.println("MFCC:\t" + Arrays.toString(mfcc));
        System.out.println("RMS:\t" + featureExtractor.getOverallAverageRMS());
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