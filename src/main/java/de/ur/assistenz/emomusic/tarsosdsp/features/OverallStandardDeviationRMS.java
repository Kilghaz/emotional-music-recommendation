package de.ur.assistenz.emomusic.tarsosdsp.features;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class OverallStandardDeviationRMS extends RMS {

    @Override
    public String getFeatureName() {
        return "Overall Standard Deviation RMS";
    }

    @Override
    public void processingFinished() {
        StandardDeviation standardDeviation = new StandardDeviation();
        double[] windowValues = new double[this.getWindowValues().size()];
        for(int i = 0; i < this.getWindowValues().size(); i++) {
            windowValues[i] = this.getWindowValues().get(i);
        }
        setFeatureValues(new float[] {(float) standardDeviation.evaluate(windowValues)} );
        reset();
    }

}
