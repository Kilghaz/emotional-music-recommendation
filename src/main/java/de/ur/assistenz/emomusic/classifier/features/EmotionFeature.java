package de.ur.assistenz.emomusic.classifier.features;

import be.tarsos.dsp.AudioProcessor;

public interface EmotionFeature extends AudioProcessor {

    void setup(float sampleRate, int windowSize, int windowOverlap);
    float[] getFeatureValue();
    String getFeatureName();
    String getFeatureName(int index);
    int getFeatureDimenion();

}
