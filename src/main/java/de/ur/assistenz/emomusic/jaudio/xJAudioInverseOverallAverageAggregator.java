package de.ur.assistenz.emomusic.jaudio;

public class JAudioInverseOverallAverageAggregator extends JAudioOverallAverageAggreator {

    @Override
    public double[] aggregate(JAudioFeatureExtractor featureExtractor) {
        double[] overallAverage = super.aggregate(featureExtractor);
        for(int i = 0; i < overallAverage.length; i++) {
            overallAverage[i] = -overallAverage[i];
        }
        return overallAverage;
    }

    @Override
    public String getName() {
        return "Overall Inverse Average";
    }
}
