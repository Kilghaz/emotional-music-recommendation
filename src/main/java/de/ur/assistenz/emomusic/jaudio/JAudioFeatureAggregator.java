package de.ur.assistenz.emomusic.jaudio;

public abstract class JAudioFeatureAggregator {

    public abstract double[] aggregate(JAudioFeatureExtractor featureExtractor);
    public abstract String getName();

    protected double[][] flip(double[][] values) {
        double[][] flipped = new double[values[0].length][values.length];
        for(int i = 0; i < flipped.length; i++) {
            for(int j = 0; j < flipped[i].length; j++) {
                flipped[i][j] = values[j][i];
            }
        }
        return flipped;
    }

}
