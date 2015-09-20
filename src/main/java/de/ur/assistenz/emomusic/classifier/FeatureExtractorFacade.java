package de.ur.assistenz.emomusic.classifier;

import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;
import jAudioFeatureExtractor.AudioFeatures.MagnitudeSpectrum;
import jAudioFeatureExtractor.jAudioTools.AudioSamples;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

public class FeatureExtractorFacade {

    private boolean normalise = false;
    private double samplingRate = 0.0;
    private int windowSize = 0;
    private double windowOverlap = 0.0;
    private FeatureExtractor extractor;

    private static final HashMap<String, FeatureExtractor> EXTRACTORS;

    static {
        EXTRACTORS = new HashMap<>();
        EXTRACTORS.put("Magnitude Spectrum", new MagnitudeSpectrum());
        EXTRACTORS.put("Power Spectrum", new MagnitudeSpectrum());
    }

    public FeatureExtractorFacade(int windowSize, double windowOverlap, double samplingRate, FeatureExtractor extractor) {
        this.normalise = false;
        this.samplingRate = samplingRate;
        this.windowSize = windowSize;
        this.windowOverlap = windowOverlap;
        this.extractor = extractor;
    }

    private int[] calculateWindowStartIndices(double[] samples) {
        LinkedList<Integer> windowStartIndicesList = new LinkedList<>();
        int start = 0;
        while (start < samples.length) {
            windowStartIndicesList.add(start);
            start += windowSize - windowOverlap;
        }
        Integer[] windowStartIndicesI = windowStartIndicesList.toArray(new Integer[1]);
        int[] windowStartIndices = new int[windowStartIndicesI.length];
        for (int i = 0; i < windowStartIndices.length; i++)
            windowStartIndices[i] = windowStartIndicesI[i];
        return windowStartIndices;
    }

    private double[] extractSamples(File file) throws Exception {
        AudioInputStream originalStream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = originalStream.getFormat();
        int bitDepth = format.getSampleSizeInBits();
        if(bitDepth != 8 && bitDepth != 16) {
            bitDepth = 16;
        }

        AudioInputStream secondStream = originalStream;
        if(format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED || !format.isBigEndian()) {
            AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), bitDepth, format.getChannels(), format.getChannels() * (bitDepth / 8), format.getSampleRate(), true);
            secondStream = AudioSystem.getAudioInputStream(audioFormat, originalStream);
        }

        AudioInputStream newStream = secondStream;
        if(format.getSampleRate() != (float)this.samplingRate || bitDepth != format.getSampleSizeInBits()) {
            AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float)this.samplingRate, bitDepth, format.getChannels(), format.getChannels() * (bitDepth / 8), format.getSampleRate(), true);
            newStream = AudioSystem.getAudioInputStream(audioFormat, secondStream);
        }

        AudioSamples audioSamples = new AudioSamples(newStream, file.getPath(), false);
        if(this.normalise) {
            audioSamples.normalizeMixedDownSamples();
        }

        return audioSamples.getSamplesMixedDown();
    }

    private HashMap<String, double[][]> calculateDependencies(File file) {
        if(this.extractor.getDepenedencies() == null) {
            return null;
        }
        HashMap<String, double[][]> depenencies = new HashMap<>();
        for(String dependencyName : this.extractor.getDepenedencies()){
            FeatureExtractorFacade dependetFeature = createDependencyExtractorFacade(dependencyName);
            depenencies.put(dependencyName, dependetFeature.extract(file));
        }
        return depenencies;
    }

    private FeatureExtractorFacade createDependencyExtractorFacade(String dependencyName) {
        return new FeatureExtractorFacade(this.windowSize, this.windowOverlap, this.samplingRate, EXTRACTORS.get(dependencyName));
    }

    private double[] calculateSampleWindow(double[] samples, int[] windowStartIndices, int windowIndex) {
        double[] sampleWindow = new double[this.windowSize];
        int windowStartIndex = windowStartIndices[windowIndex];
        int endSample = windowStartIndex + this.windowSize - 1;
        if(endSample < samples.length) {
            System.arraycopy(samples, windowStartIndex, sampleWindow, 0, endSample + 1 - windowStartIndex);
        } else {
            for(int i = windowStartIndex; i <= endSample; ++i) {
                if(i < samples.length) {
                    sampleWindow[i - windowStartIndex] = samples[i];
                } else {
                    sampleWindow[i - windowStartIndex] = 0.0D;
                }
            }
        }
        return sampleWindow;
    }

    private double[][] createOtherFeatureValues(HashMap<String, double[][]> dependencies, int windowIndex) {
        if(dependencies == null) {
            return null;
        }
        String[] featureExtractorDependencies = extractor.getDepenedencies();
        double[][] otherFeatureValues = new double[featureExtractorDependencies.length][];
        int i = 0;
        for(String dependencyName : this.extractor.getDepenedencies()) {
            int offset = this.extractor.getDepenedencyOffsets()[i];
            otherFeatureValues[i] = dependencies.get(dependencyName)[windowIndex + offset];
            i++;
        }
        return otherFeatureValues;
    }

    private double[][] extractFeature(File file) throws Exception {
        HashMap<String, double[][]> depdencies = calculateDependencies(file);

        double[] samples = extractSamples(file);
        int[] windowStartIndices = calculateWindowStartIndices(samples);
        double[][] results = new double[windowStartIndices.length][];

        for(int windowIndex = 0; windowIndex < windowStartIndices.length; ++windowIndex) {
            double[] sampleWindow = calculateSampleWindow(samples, windowStartIndices, windowIndex);
            double[][] otherFeatureValues = createOtherFeatureValues(depdencies, windowIndex);
            results[windowIndex] = this.extractor.extractFeature(sampleWindow, this.samplingRate, otherFeatureValues);
        }

        return results;
    }

    public double[][] extract(File file) {
        try {
            return extractFeature(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}