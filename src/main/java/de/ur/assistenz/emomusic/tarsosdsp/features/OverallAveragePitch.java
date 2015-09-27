package de.ur.assistenz.emomusic.tarsosdsp.features;

import be.tarsos.dsp.pitch.PitchProcessor;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

import java.util.List;

public class OverallAveragePitch extends Pitch {

    public OverallAveragePitch() {
        super(PitchProcessor.PitchEstimationAlgorithm.DYNAMIC_WAVELET);
    }

    @Override
    public void processingFinished() {
        Mean mean = new Mean();
        List<Float> pitchValuesList = getPitchValues();
        double[] pitchValues = new double[pitchValuesList.size()];
        for(int i = 0; i < pitchValues.length; i++) {
            pitchValues[i] = pitchValuesList.get(i);
        }
        setFeatureValues(new float[] {(float)mean.evaluate(pitchValues)} );
        reset();
    }

    @Override
    public String getFeatureName() {
        return "Overall Average Pitch";
    }

    @Override
    public String getFeatureName(int index) {
        return getFeatureName();
    }

}
