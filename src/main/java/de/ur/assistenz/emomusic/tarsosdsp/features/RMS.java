package de.ur.assistenz.emomusic.tarsosdsp.features;

import be.tarsos.dsp.AudioEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class RMS implements TarsosDSPAudioProcessor {

    private float value = 0;
    private int windowCount = 0;

    private float[] featureValues;
    private List<Float> windowValues = new ArrayList<>();

    @Override
    public boolean process(AudioEvent audioEvent) {
        this.windowValues.add((float) audioEvent.getRMS());
        windowCount++;
        return true;
    }

    @Override
    public void setup(float sampleRate, int windowSize, int windowOverlap) {
        // no need to setup this extractor
    }

    @Override
    public float[] getFeatureValue() {
        return this.featureValues;
    }

    @Override
    public String getFeatureName(int index) {
        return getFeatureName();
    }

    @Override
    public int getFeatureDimenion() {
        return 1;
    }

    protected int getWindowCount() {
        return windowCount;
    }

    protected List<Float> getWindowValues() {
        return windowValues;
    }

    protected void reset() {
        this.windowCount = 0;
        this.value = 0;
        this.windowValues = new ArrayList<>();
    }

    protected void setFeatureValues(float[] featureValues) {
        this.featureValues = featureValues;
    }
}
