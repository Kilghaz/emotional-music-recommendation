package de.ur.assistenz.emomusic.tarsosdsp.features;

import be.tarsos.dsp.AudioProcessor;

public interface TarsosDSPAudioProcessor extends AudioProcessor {

    void setup(float sampleRate, int windowSize, int windowOverlap);
    float[] getFeatureValue();
    String getFeatureName();
    String getFeatureName(int index);
    int getFeatureDimenion();

}
