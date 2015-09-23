package de.ur.assistenz.emomusic.classifier.features;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class OverallStandardDeviationMFCC extends MFCC {

    public OverallStandardDeviationMFCC(int coefficients, float filterFrequencyLow, float filterFrequencyHigh) {
        super(coefficients, filterFrequencyLow, filterFrequencyHigh);
    }

    @Override
    public String getFeatureName() {
        return "Overall Standard Deviation MFCC";
    }

    @Override
    public void processingFinished() {
        StandardDeviation standardDeviation = new StandardDeviation();
        float[] overallSDMFCC = new float[this.getCoefficients()];
        for(int i = 0; i < overallSDMFCC.length; i++) {
            overallSDMFCC[i] = 0;
        }
        for(int i = 0; i < overallSDMFCC.length; i++) {
            double[] cumulativeWindowValues = new double[this.getWindowValues().size()];
            for (int j = 0; j < this.getWindowValues().size(); j++) {
                cumulativeWindowValues[j] = this.getWindowValues().get(j)[i];
            }
            overallSDMFCC[i] = (float) standardDeviation.evaluate(cumulativeWindowValues);
        }
        this.setFeatureValues(overallSDMFCC);
        this.reset();
    }

}
