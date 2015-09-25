package de.ur.assistenz.emomusic.jaudio;

import org.apache.commons.math3.stat.descriptive.moment.Mean;

public class JAudioOverallAverageAggreator extends JAudioFeatureAggregator {

    private Mean mean = new Mean();

    @Override
    public double[] aggregate(JAudioFeatureExtractor featureExtractor) {
        double[][] flipped = flip(featureExtractor.getFeatureValues());
        double[] result = new double[flipped.length];
        for(int i = 0; i < flipped.length; i++) {
            result[i] = mean.evaluate(flipped[i]);
        }
        return result;
    }

    @Override
    public String getName() {
        return "Overall Average";
    }
}
