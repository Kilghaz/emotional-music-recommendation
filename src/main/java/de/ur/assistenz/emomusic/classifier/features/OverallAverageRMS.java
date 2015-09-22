package de.ur.assistenz.emomusic.classifier.features;

import be.tarsos.dsp.AudioEvent;

public class OverallAverageRMS implements EmotionFeature {

    private float value = 0;
    private float overallAverageRMS = 0;
    private int windowCount = 0;

    @Override
    public boolean process(AudioEvent audioEvent) {
        value += audioEvent.getRMS();
        windowCount++;
        return true;
    }

    @Override
    public void processingFinished() {
        overallAverageRMS = value / windowCount;
        windowCount = 0;
        value = 0;
    }

    @Override
    public void setup(float sampleRate, int windowSize, int windowOverlap) {
        // no need to setup this extractor
    }

    @Override
    public float[] getFeatureValue() {
        return new float[]{ overallAverageRMS };
    }

    @Override
    public String getFeatureName() {
        return "Overall Average RMS";
    }

    @Override
    public String getFeatureName(int index) {
        return getFeatureName();
    }

    @Override
    public int getFeatureDimenion() {
        return 1;
    }

    public float getOverallAverageRMS() {
        return overallAverageRMS;
    }

}
