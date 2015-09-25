package de.ur.assistenz.emomusic.jaudio;

import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;

import java.util.ArrayList;
import java.util.List;

public class JAudioFeatureExtractor {

    private static final JAudioFeatureExtractorFactory FEATURE_EXTRACTOR_FACTORY = new JAudioFeatureExtractorFactory();

    private FeatureExtractor extractor;
    private List<JAudioFeatureExtractor> dependencies = new ArrayList<>();
    private List<double[]> featureValues = new ArrayList<>();
    private double[] aggreatedFeatureValues;
    private JAudioFeatureAggregator aggregator = new JAudioOverallAverageAggreator();

    public JAudioFeatureExtractor(FeatureExtractor featureExtractor) {
        this.extractor = featureExtractor;
        if(hasDependencies()) for(String dependency : this.extractor.getDepenedencies()) {
            FeatureExtractor extractor = FEATURE_EXTRACTOR_FACTORY.createFeatureExtractor(dependency);
            this.dependencies.add(new JAudioFeatureExtractor(extractor));
        }
    }

    public JAudioFeatureExtractor(FeatureExtractor featureExtractor, JAudioFeatureAggregator aggregator) {
        this(featureExtractor);
        this.aggregator = aggregator;
    }

    public String getName() {
        return this.aggregator.getName() + " " + this.extractor.getFeatureDefinition().name;
    }

    public String getName(int i) {
        return getName() + " " + i;
    }

    public int getDimension() {
        return featureValues.get(0).length;
    }

    public boolean hasDependencies() {
        return this.extractor.getDepenedencies() != null;
    }

    public List<JAudioFeatureExtractor> getDependencies() {
        return dependencies;
    }

    private double[][] calculateDependencyValues(double[] window, double samplingRate) {
        double[][] values = new double[this.dependencies.size()][];
        for(int i = 0; i < values.length; i++) {
            values[i] = this.dependencies.get(i).extractFeature(window, samplingRate);
        }
        return values;
    }

    public double[] extractFeature(double[] window, double samplingRate) {
        double[][] dependencyValues = calculateDependencyValues(window, samplingRate);
        try {
            double[] windowValues = this.extractor.extractFeature(window, samplingRate, dependencyValues);
            featureValues.add(windowValues);
            return windowValues;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void reset() {
        this.featureValues = new ArrayList<>();
        for(JAudioFeatureExtractor dependency : dependencies) {
            dependency.reset();
        }
    }

    public double[][] getFeatureValues() {
        return featureValues.toArray(new double[featureValues.size()][]);
    }

    public double[] getAggreatedFeatureValues() {
        return aggreatedFeatureValues;
    }

    protected void postProcess() {
        this.aggreatedFeatureValues = this.aggregator.aggregate(this);
    }

}
