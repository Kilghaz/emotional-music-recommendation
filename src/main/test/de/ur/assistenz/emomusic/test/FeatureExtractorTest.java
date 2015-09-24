package de.ur.assistenz.emomusic.test;

import de.ur.assistenz.emomusic.classifier.EmotionClassifier;
import de.ur.assistenz.emomusic.classifier.FeatureExtractor;
import de.ur.assistenz.emomusic.classifier.features.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

public class FeatureExtractorTest {

    @Test
    public void testExtractMP3() throws Exception {
        FeatureExtractor featureExtractor = new EmotionClassifier().createFeatureExtractorInstance();
        featureExtractor.extract(new File("test-resources/test_audio.mp3"));
        for(EmotionFeature feature : featureExtractor.getFeatures()) {
            float[] values = feature.getFeatureValue();
            testFeature(values);
            System.out.println(feature.getFeatureName() + ":\t" + Arrays.toString(values));
        }
    }

    @Test
    public void testExtractWAV() throws Exception {
        FeatureExtractor featureExtractor = new EmotionClassifier().createFeatureExtractorInstance();
        featureExtractor.extract(new File("test-resources/test_audio.wav"));
        for(EmotionFeature feature : featureExtractor.getFeatures()) {
            float[] values = feature.getFeatureValue();
            testFeature(values);
            System.out.println(feature.getFeatureName() + ":\t" + Arrays.toString(values));
        }
    }

    private void testFeature(float[] values) {
        Assert.assertNotNull(values);
        assertArrayValuesNot(values, 0);
        assertArrayValuesNot(values, Float.POSITIVE_INFINITY);
        assertArrayValuesNot(values, Float.NEGATIVE_INFINITY);
        assertArrayValuesNot(values, Float.NaN);
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