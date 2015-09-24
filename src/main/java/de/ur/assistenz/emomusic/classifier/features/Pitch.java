package de.ur.assistenz.emomusic.classifier.features;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

import java.util.*;
import java.util.stream.Stream;

public abstract class Pitch implements EmotionFeature, PitchDetectionHandler {

    private static final float NO_PITCH_DETECTED = -1.0f;

    private PitchProcessor pitchProcessor;
    private PitchProcessor.PitchEstimationAlgorithm estimationAlgorithm;

    private List<Float> pitchValues = new ArrayList<>();

    private float[] featureValues;

    public Pitch(PitchProcessor.PitchEstimationAlgorithm estimationAlgorithm) {
        this.estimationAlgorithm = estimationAlgorithm;
    }

    @Override
    public void setup(float sampleRate, int windowSize, int windowOverlap) {
        pitchProcessor = new PitchProcessor(this.estimationAlgorithm, sampleRate, windowSize, this);
    }

    @Override
    public float[] getFeatureValue() {
        return this.featureValues;
    }

    @Override
    public int getFeatureDimenion() {
        return this.getFeatureValue().length;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        try {
            pitchProcessor.process(audioEvent);
        }
        catch (AssertionError e) {
            return false;
        }
        return true;
    }

    protected void reset() {
        this.pitchValues = new ArrayList<>();
    }

    protected void setFeatureValues(float[] featureValues) {
        this.featureValues = featureValues;
    }

    protected List<Float> getPitchValues() {
        return pitchValues;
    }

    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        float pitch = pitchDetectionResult.getPitch();
        if(pitch == NO_PITCH_DETECTED)
            return;
        this.pitchValues.add(pitch);
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        Map<K,V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K,V>> stream = map.entrySet().stream();

        stream.sorted(Comparator.comparing(Map.Entry::getValue))
                .forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }

}
