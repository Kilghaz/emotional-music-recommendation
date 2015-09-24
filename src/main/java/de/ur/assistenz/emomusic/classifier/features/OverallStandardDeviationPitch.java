package de.ur.assistenz.emomusic.classifier.features;

import be.tarsos.dsp.pitch.PitchProcessor;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.List;

public class OverallStandardDeviationPitch extends Pitch {

    public OverallStandardDeviationPitch() {
        super(PitchProcessor.PitchEstimationAlgorithm.DYNAMIC_WAVELET);
    }

    @Override
    public String getFeatureName() {
        return "Overall Standard Deviation Pitch";
    }

    @Override
    public String getFeatureName(int index) {
        return getFeatureName();
    }

    @Override
    public void processingFinished() {
        StandardDeviation standardDeviation = new StandardDeviation();
        List<Float> pitchValuesList = getPitchValues();
        double[] pitchValues = new double[pitchValuesList.size()];
        for(int i = 0; i < pitchValues.length; i++) {
            pitchValues[i] = pitchValuesList.get(i);
        }
        setFeatureValues(new float[] {(float)standardDeviation.evaluate(pitchValues)} );
        reset();
    }

}
