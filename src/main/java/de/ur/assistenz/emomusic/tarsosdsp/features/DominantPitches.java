package de.ur.assistenz.emomusic.tarsosdsp.features;

import be.tarsos.dsp.pitch.PitchProcessor;

import java.util.*;
import java.util.stream.Stream;

public class DominantPitches extends Pitch {

    private static final HashMap<Float, String> NOTES = new HashMap<>();
    private static final float NO_PITCH_DETECTED = -1.0f;

    static {
        NOTES.put(440.000f, "A4");
        NOTES.put(466.164f, "A#4");
        NOTES.put(493.883f, "B4");
        NOTES.put(523.251f, "C5");
        NOTES.put(554.365f, "C#5");
        NOTES.put(587.330f, "D5");
        NOTES.put(622.254f, "D#5");
        NOTES.put(659.255f, "E5");
        NOTES.put(698.456f, "F5");
        NOTES.put(739.989f, "F#5");
        NOTES.put(783.991f, "G5");
        NOTES.put(830.609f, "G#5");
        NOTES.put(880.000f, "A5");
        NOTES.put(932.328f, "A#5");
        NOTES.put(987.767f, "B5");
        NOTES.put(1046.50f, "C6");
        NOTES.put(1108.73f, "C#6");
        NOTES.put(1174.66f, "D6");
        NOTES.put(1244.51f, "D#6");
        NOTES.put(1318.51f, "E6");
        NOTES.put(1396.91f, "F6");
        NOTES.put(1479.98f, "F#6");
        NOTES.put(1567.98f, "G6");
        NOTES.put(1661.22f, "G#6");
        NOTES.put(1760.00f, "A6");
        NOTES.put(1864.66f, "A#6");
        NOTES.put(1975.53f, "B6");
        NOTES.put(2093.00f, "C7");
        NOTES.put(2217.46f, "C#7");
        NOTES.put(2349.32f, "D7");
        NOTES.put(2489.02f, "D#8");
        NOTES.put(2637.02f, "E7");
        NOTES.put(2793.83f, "F7");
        NOTES.put(2959.96f, "F#7");
        NOTES.put(3135.96f, "G7");
        NOTES.put(3322.44f, "G#7");
        NOTES.put(3520.00f, "A7");
        NOTES.put(3729.31f, "A#7");
        NOTES.put(3951.07f, "B7");
    }


    private int pitchCounts = 0;

    public DominantPitches(int pitchCounts) {
        super(PitchProcessor.PitchEstimationAlgorithm.DYNAMIC_WAVELET);
        this.pitchCounts = pitchCounts;
    }

    @Override
    public String getFeatureName() {
        return "Dominant Pitch";
    }

    @Override
    public String getFeatureName(int index) {
        return getFeatureName() + " " + index;
    }

    @Override
    public void processingFinished() {
        HashMap<String, Integer> noteCounts = new HashMap<>();
        for(float pitch : this.getPitchValues()) {
            String note = getMostSimilarNote(pitch);
            if(noteCounts.get(note) == null) {
                noteCounts.put(note, 0);
            }
            noteCounts.put(note, noteCounts.get(note) + 1);
        }
        Map<String, Integer> sortedCounts = sortByValue(noteCounts);
        List<String> keys = new ArrayList<>(sortedCounts.keySet());
        List<Float> dominantPitches = new ArrayList<>();
        for(int i = keys.size() - 1; i >= Math.max(keys.size() - 1 - this.pitchCounts, 0); i--) {
            dominantPitches.add(getPitch(keys.get(i)));
        }
        float[] result = new float[dominantPitches.size()];
        for(int i = 0; i < dominantPitches.size(); i++) {
            result[i] = dominantPitches.get(i);
        }
        this.setFeatureValues(result);
        reset();
    }

    private float getPitch(String note) {
        for(float notePitch : NOTES.keySet()) {
            if(NOTES.get(notePitch).equals(note)) {
                return notePitch;
            }
        }
        return -1;
    }

    private String getMostSimilarNote(float pitch) {
        String note = null;
        float minDistance = Float.MAX_VALUE;
        for(float notePitch : NOTES.keySet()) {
            float distance = Math.abs(pitch - notePitch);
            if(distance < minDistance || note == null) {
                note = NOTES.get(notePitch);
                minDistance = distance;
            }
        }
        return note;
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        Map<K,V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K,V>> stream = map.entrySet().stream();

        stream.sorted(Comparator.comparing(Map.Entry::getValue))
                .forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }

}
