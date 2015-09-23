package de.ur.assistenz.emomusic.test;

import de.ur.assistenz.emomusic.classifier.FeatureExtractor;
import de.ur.assistenz.emomusic.classifier.features.EmotionFeature;
import de.ur.assistenz.emomusic.classifier.features.DominantPitches;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

public class DominantPitchesTest {

    @Test
    public void pitchExtractionTest() throws Exception {
        FeatureExtractor featureExtractor = new FeatureExtractor(512, 0);
        featureExtractor.addFeature(new DominantPitches(5));
        featureExtractor.extract(new File("test-resources/test_audio.mp3"));
        for(EmotionFeature feature : featureExtractor.getFeatures()) {
            float[] values = feature.getFeatureValue();
            testFeature(values);
            System.out.println(Arrays.toString(values));
        }
    }

    private void testFeature(float[] values) {
        Assert.assertNotEquals(values.length, 0);
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