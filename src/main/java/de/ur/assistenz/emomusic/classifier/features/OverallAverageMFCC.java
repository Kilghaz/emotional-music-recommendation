package de.ur.assistenz.emomusic.classifier.features;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.mfcc.MFCC;

import java.util.ArrayList;
import java.util.List;

public class OverallAverageMFCC implements EmotionFeature {

    private int coefficients;
    private float filterFrequencyLow;
    private float filterFrequencyHigh;

    private List<float[]> windowValues = new ArrayList<>();
    private int windowCount = 0;
    private float[] overallAverageMFCC;

    private MFCC mfccProcessor;

    public OverallAverageMFCC(int coefficients, float filterFrequencyLow, float filterFrequencyHigh) {
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
    public void processingFinished() {
        mfccProcessor.processingFinished();
        float[] overallAverageMFCC = new float[this.coefficients];
        for(int i = 0; i < overallAverageMFCC.length; i++) {
            overallAverageMFCC[i] = 0;
        }
        for(float[] values : windowValues) {
            for(int i = 0; i < overallAverageMFCC.length; i++) {
                overallAverageMFCC[i] += values[i];
            }
        }
        for(int i = 0; i < overallAverageMFCC.length; i++) {
            overallAverageMFCC[i] /= this.windowCount;
        }
        this.overallAverageMFCC = overallAverageMFCC;
        this.windowValues = new ArrayList<>();
        this.windowCount = 0;
    }

    public float[] getOverallAverageMFCC() {
        return overallAverageMFCC;
    }

    @Override
    public void setup(float sampleRate, int windowSize, int windowOverlap) {
        this.mfccProcessor = new MFCC(windowSize, sampleRate, this.coefficients, this.coefficients, this.filterFrequencyLow, this.filterFrequencyHigh);
    }

    @Override
    public float[] getFeatureValue() {
        return this.overallAverageMFCC;
    }

    @Override
    public String getFeatureName() {
        return "Overall Average MFCC";
    }

    @Override
    public String getFeatureName(int index) {
        return getFeatureName() + " " + index;
    }

    @Override
    public int getFeatureDimenion() {
        return this.overallAverageMFCC.length;
    }
}
