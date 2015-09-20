package de.ur.assistenz.emomusic.classifier;

import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;
import jAudioFeatureExtractor.jAudioTools.AudioSamples;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.LinkedList;

public class SimpleFeatureProcessor {

    private boolean normalise = false;
    private double samplingRate = 0.0;
    private int windowSize = 0;
    private double windowOverlap = 0.0;
    private FeatureExtractor extractor;

    public SimpleFeatureProcessor(int windowSize, double windowOverlap, double samplingRate, FeatureExtractor extractor) {
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
        AudioInputStream original_stream = AudioSystem.getAudioInputStream(file);
        AudioFormat original_format = original_stream.getFormat();
        int bit_depth = original_format.getSampleSizeInBits();
        if(bit_depth != 8 && bit_depth != 16) {
            bit_depth = 16;
        }

        AudioInputStream second_stream = original_stream;
        if(original_format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED || !original_format.isBigEndian()) {
            AudioFormat new_stream = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, original_format.getSampleRate(), bit_depth, original_format.getChannels(), original_format.getChannels() * (bit_depth / 8), original_format.getSampleRate(), true);
            second_stream = AudioSystem.getAudioInputStream(new_stream, original_stream);
        }

        AudioInputStream new_stream1 = second_stream;
        if(original_format.getSampleRate() != (float)this.samplingRate || bit_depth != original_format.getSampleSizeInBits()) {
            AudioFormat audio_data = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float)this.samplingRate, bit_depth, original_format.getChannels(), original_format.getChannels() * (bit_depth / 8), original_format.getSampleRate(), true);
            new_stream1 = AudioSystem.getAudioInputStream(audio_data, second_stream);
        }

        AudioSamples audio_data1 = new AudioSamples(new_stream1, file.getPath(), false);
        if(this.normalise) {
            audio_data1.normalizeMixedDownSamples();
        }

        return audio_data1.getSamplesMixedDown();
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

    private double[][][] getFeatures(double[] samples, int[] windowStartIndices) throws Exception {
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
                FeatureExtractor feature = this.extractor;
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

    public double[][][] process(File file) {
        try {
            double[] samples = extractSamples(file);
            int[] windowStartIndices = calculateWindowStartIndices(samples);
            return getFeatures(samples, windowStartIndices);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}