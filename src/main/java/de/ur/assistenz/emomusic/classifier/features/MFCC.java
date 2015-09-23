package de.ur.assistenz.emomusic.classifier.features;

import be.tarsos.dsp.AudioEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class MFCC implements EmotionFeature {

    private int coefficients;
    private float filterFrequencyLow;
    private float filterFrequencyHigh;

    private List<float[]> windowValues = new ArrayList<>();
    private int windowCount = 0;
    private float[] featureValues;

    private be.tarsos.dsp.mfcc.MFCC mfccProcessor;

    public MFCC(int coefficients, float filterFrequencyLow, float filterFrequencyHigh) {
        this.coefficients = coefficients;
        this.filterFrequencyLow = filterFrequencyLow;
        this.filterFrequencyHigh = filterFrequencyHigh;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        mfccProcessor.process(audioEvent);
        this.windowValues.add(mfccProcessor.getMFCC());
        this.windowCount++;
        return true;
    }

    @Override
    public void setup(float sampleRate, int windowSize, int windowOverlap) {
        this.mfccProcessor = new be.tarsos.dsp.mfcc.MFCC(windowSize, sampleRate, this.coefficients, this.coefficients, this.filterFrequencyLow, this.filterFrequencyHigh);
    }

    @Override
    public float[] getFeatureValue() {
        return this.featureValues;
    }

    @Override
    public String getFeatureName(int index) {
        return getFeatureName() + " " + index;
    }

    @Override
    public int getFeatureDimenion() {
        return this.featureValues.length;
    }

    public int getCoefficients() {
        return coefficients;
    }

    public float getFilterFrequencyHigh() {
        return filterFrequencyHigh;
    }

    public float getFilterFrequencyLow() {
        return filterFrequencyLow;
    }

    protected int getWindowCount() {
        return windowCount;
    }

    protected List<float[]> getWindowValues() {
        return windowValues;
    }

    protected void setFeatureValues(float[] featureValues) {
        this.featureValues = featureValues;
    }

    protected void reset() {
        this.windowValues = new ArrayList<>();
        this.windowCount = 0;
    }
}
