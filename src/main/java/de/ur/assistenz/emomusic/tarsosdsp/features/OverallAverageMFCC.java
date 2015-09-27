package de.ur.assistenz.emomusic.tarsosdsp.features;

public class OverallAverageMFCC extends MFCC {

    public OverallAverageMFCC(int coefficients, float filterFrequencyLow, float filterFrequencyHigh) {
        super(coefficients, filterFrequencyLow, filterFrequencyHigh);
    }

    @Override
    public void processingFinished() {
        float[] overallAverageMFCC = new float[this.getCoefficients()];
        for(int i = 0; i < overallAverageMFCC.length; i++) {
            overallAverageMFCC[i] = 0;
        }
        for(float[] values : this.getWindowValues()) {
            for(int i = 0; i < overallAverageMFCC.length; i++) {
                overallAverageMFCC[i] += values[i];
            }
        }
        for(int i = 0; i < overallAverageMFCC.length; i++) {
            overallAverageMFCC[i] /= this.getWindowCount();
        }
        this.reset();
        this.setFeatureValues(overallAverageMFCC);
    }

    @Override
    public String getFeatureName() {
        return "Overall Average MFCC (" + this.getFilterFrequencyLow() + "-" + this.getFilterFrequencyHigh() + ")";
    }

}
