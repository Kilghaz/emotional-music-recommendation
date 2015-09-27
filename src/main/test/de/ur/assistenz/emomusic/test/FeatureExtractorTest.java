package de.ur.assistenz.emomusic.test;

import de.ur.assistenz.emomusic.classifier.EmotionClassifier;
import de.ur.assistenz.emomusic.tarsosdsp.FeatureExtractor;
import de.ur.assistenz.emomusic.tarsosdsp.features.TarsosDSPAudioProcessor;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

public class FeatureExtractorTest {

    @Test
    public void testExtractMP3() throws Exception {
        testExtract(new File("test-resources/test_audio.mp3"));
    }

    @Test
    public void testExtractWAV() throws Exception {
        testExtract(new File("test-resources/test_audio.wav"));
    }

    private void testExtract(File file) throws Exception {
        FeatureExtractor featureExtractor = new EmotionClassifier().createFeatureExtractorInstance();
        featureExtractor.extract(file);
        for(TarsosDSPAudioProcessor feature : featureExtractor.getFeatures()) {
            float[] values = feature.getFeatureValue();
            testFeature(values);
            System.out.println(feature.getFeatureName() + ":\n" + Arrays.toString(values) + "\n");
        }
    }

    private void testFeature(float[] values) {
        assert values != null;
        assertArrayValuesNot(values, 0);
        assertArrayValuesNot(values, Float.POSITIVE_INFINITY);
        assertArrayValuesNot(values, Float.NEGATIVE_INFINITY);
        assertArrayValuesNot(values, Float.NaN);
    }

    private void assertArrayValuesNot(float[] array, float value) {
        for (float val : array) {
            assert val != value;
        }
    }

}