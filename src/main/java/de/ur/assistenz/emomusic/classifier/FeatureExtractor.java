package de.ur.assistenz.emomusic.classifier;

import jAudioFeatureExtractor.jAudioTools.AudioSamples;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.LinkedList;

public class FeatureExtractor {

    private boolean normalise = false;
    private double samplingRate = 0.0;
    private int windowSize = 0;
    private double windowOverlap = 0.0;
    private jAudioFeatureExtractor.AudioFeatures.FeatureExtractor extractor;

    public FeatureExtractor(int windowSize, double windowOverlap, double samplingRate, jAudioFeatureExtractor.AudioFeatures.FeatureExtractor extractor) {
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

    private int[] createFeatureExtractorDependencies() {
        String[] dependencyNames = new String[1];

        dependencyNames[0] = extractor.getFeatureDefinition().name;

        String[] dependencies = this.extractor.getDepenedencies();
        if(dependencies != null) {
            int[] featureExtractorDependencies = new int[dependencies.length];

            for(int theseOffsets = 0; theseOffsets < dependencies.length; ++theseOffsets) {
                for(int i = 0; i < dependencyNames.length; ++i) {
                    if(dependencies[theseOffsets].equals(dependencyNames[i])) {
                        featureExtractorDependencies[theseOffsets] = i;
                    }
                }
            }
            return featureExtractorDependencies;
        }
        return null;
    }

    private int[] calculateMaxFeatureOffsets(int[] featureExtractorDependencies) {
        int[] maxFeatureOffsets = new int[1];

        if(this.extractor.getDepenedencyOffsets() == null) {
            maxFeatureOffsets[0] = 0;
        } else {
            int[] depenedencyOffsets = this.extractor.getDepenedencyOffsets();

            maxFeatureOffsets[0] = Math.abs(depenedencyOffsets[0] + maxFeatureOffsets[featureExtractorDependencies[0]]);

            for(int i = 0; i < depenedencyOffsets.length; ++i) {
                int val = Math.abs(depenedencyOffsets[i]) + maxFeatureOffsets[featureExtractorDependencies[i]];
                if(val > maxFeatureOffsets[0]) {
                    maxFeatureOffsets[0] = val;
                }
            }
        }
        return maxFeatureOffsets;
    }

    private double[][][] extractFeature(double[] samples, int[] windowStartIndices) throws Exception {
        double[][][] results = new double[windowStartIndices.length][1][];

        for(int win = 0; win < windowStartIndices.length; ++win) {

            double[] window = new double[this.windowSize];
            int windowStartIndex = windowStartIndices[win];
            int endSample = windowStartIndex + this.windowSize - 1;
            if(endSample < samples.length) {
                System.arraycopy(samples, windowStartIndex, window, 0, endSample + 1 - windowStartIndex);
            } else {
                for(int i = windowStartIndex; i <= endSample; ++i) {
                    if(i < samples.length) {
                        window[i - windowStartIndex] = samples[i];
                    } else {
                        window[i - windowStartIndex] = 0.0D;
                    }
                }
            }

            int[] featureExtractorDependencies = createFeatureExtractorDependencies();
            int[] maxFeatureOffsets = calculateMaxFeatureOffsets(featureExtractorDependencies);

            if(win < maxFeatureOffsets[0]) {
                results[win][0] = null;
            } else {
                jAudioFeatureExtractor.AudioFeatures.FeatureExtractor feature = this.extractor;
                double[][] otherFeatureValues = null;
                if(featureExtractorDependencies != null) {
                    otherFeatureValues = new double[featureExtractorDependencies.length][];

                    for(int i = 0; i < featureExtractorDependencies.length; ++i) {
                        int dependency = featureExtractorDependencies[i];
                        int offset = feature.getDepenedencyOffsets()[i];
                        otherFeatureValues[i] = results[win + offset][dependency];
                    }
                }

                results[win][0] = feature.extractFeature(window, this.samplingRate, otherFeatureValues);
            }
        }

        return results;
    }

    public double[][] extract(File file) {
        try {
            double[] samples = extractSamples(file);
            int[] windowStartIndices = calculateWindowStartIndices(samples);
            return extractFeature(samples, windowStartIndices)[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}