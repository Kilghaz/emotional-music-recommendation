package de.ur.assistenz.emomusic.tarsosdsp.features;

public class OverallAverageRMS extends RMS {

    @Override
    public void processingFinished() {
        float overallAverageRMS = 0;
        for(float value : this.getWindowValues()) {
            overallAverageRMS += value;
        }
        overallAverageRMS /= this.getWindowCount();
        setFeatureValues(new float[] { overallAverageRMS });
        reset();
    }

    @Override
    public String getFeatureName() {
        return "Overall Average RMS";
    }

}
