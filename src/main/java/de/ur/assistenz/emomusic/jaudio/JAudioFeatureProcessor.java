package de.ur.assistenz.emomusic.jaudio;

import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;
import jAudioFeatureExtractor.jAudioTools.AudioSamples;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JAudioFeatureProcessor {

    private JAudioFeatureExtractorFactory featureExtractorFactory = new JAudioFeatureExtractorFactory();

    private boolean normalise = false;
    private double samplingRate = 0.0;
    private int windowSize = 0;
    private double windowOverlap = 0.0;
    private List<JAudioFeatureExtractor> featureExtractors = new ArrayList<>();

    public JAudioFeatureProcessor(int windowSize, double windowOverlap) {
        this.normalise = false;
        this.windowSize = windowSize;
        this.windowOverlap = windowOverlap;
    }

    public void addFeatureExtractor(FeatureExtractor featureExtractor, JAudioFeatureAggregator aggregator) {
        featureExtractors.add(new JAudioFeatureExtractor(featureExtractor, aggregator));
    }

    public void addFeatureExtractor(FeatureExtractor featureExtractor) {
        featureExtractors.add(new JAudioFeatureExtractor(featureExtractor));
    }

    public void addFeatureExtractor(JAudioFeatureExtractor featureExtractor) {
        featureExtractors.add(featureExtractor);
    }

    public void removeFeatureExtractor(JAudioFeatureExtractor featureExtractor) {
        featureExtractors.remove(featureExtractor);
    }

    private int[] calculateWindowStartIndices(double[] samples) {
        LinkedList<Integer> windowStartIndicesList = new LinkedList<>();
        int start = 0;
        while (start < samples.length) {
            windowStartIndicesList.add(start);
            start += windowSize - windowOverlap;
        }
        int[] windowStartIndices = new int[windowStartIndicesList.size()];
        for (int i = 0; i < windowStartIndices.length; i++){
            windowStartIndices[i] = windowStartIndicesList.get(i);
        }
        return windowStartIndices;
    }

    private double[] extractSamples(File file) throws Exception {
        AudioInputStream stream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = stream.getFormat();
        this.samplingRate = format.getSampleRate();

        int bitDepth = format.getSampleSizeInBits();
        if(bitDepth != 8 && bitDepth != 16) {
            bitDepth = 16;
        }

        AudioInputStream secondStream = stream;
        if(format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED || !format.isBigEndian()) {
            AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), bitDepth, format.getChannels(), format.getChannels() * (bitDepth / 8), format.getSampleRate(), true);
            secondStream = AudioSystem.getAudioInputStream(audioFormat, stream);
        }

        AudioInputStream thirdStream = secondStream;
        if(format.getSampleRate() != (float)this.samplingRate || bitDepth != format.getSampleSizeInBits()) {
            AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float)this.samplingRate, bitDepth, format.getChannels(), format.getChannels() * (bitDepth / 8), format.getSampleRate(), true);
            thirdStream = AudioSystem.getAudioInputStream(audioFormat, secondStream);
        }

        AudioSamples audioSamples = new AudioSamples(thirdStream, file.getPath(), false);
        if(this.normalise) {
            audioSamples.normalizeMixedDownSamples();
        }

        return audioSamples.getSamplesMixedDown();
    }

    private void extractFeatures(double[] samples, int[] windowStartIndices) throws Exception {
        for(JAudioFeatureExtractor featureExtractor : featureExtractors) {
            featureExtractor.reset();
        }
        for(int windowIndex : windowStartIndices) {
            double[] window = calculateSampleWindow(windowIndex, samples);
            for(JAudioFeatureExtractor featureExtractor : featureExtractors) {
                featureExtractor.extractFeature(window, this.samplingRate);
            }
        }
        for(JAudioFeatureExtractor featureExtractor : featureExtractors) {
            featureExtractor.postProcess();
        }
    }

    private double[] calculateSampleWindow(int index, double[] samples) {
        double[] window = new double[this.windowSize];
        int endSample = index + this.windowSize - 1;
        if(endSample < samples.length) {
            System.arraycopy(samples, index, window, 0, this.windowSize);
        }
        else {
            for(int i = index; i <= endSample; ++i) {
                if(i < samples.length) {
                    window[i - index] = samples[i];
                } else {
                    window[i - index] = 0.0D;
                }
            }
        }
        return window;
    }

    public void extractFeatures(File file) {
        try {
            double[] samples = extractSamples(file);
            int[] windowStartIndices = calculateWindowStartIndices(samples);
            extractFeatures(samples, windowStartIndices);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<JAudioFeatureExtractor> getFeatures() {
        return featureExtractors;
    }

    public double getFeatureValue(String featureName) {
        for(JAudioFeatureExtractor featureExtractor : featureExtractors) {
            for(int i = 0; i < featureExtractor.getDimension(); i++) {
                if(featureExtractor.getName(i).equals(featureName)) {
                    return featureExtractor.getAggreatedFeatureValues()[i];
                }
            }
        }
        return Double.MIN_VALUE;
    }

}
