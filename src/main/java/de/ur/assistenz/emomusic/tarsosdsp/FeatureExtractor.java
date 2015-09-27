package de.ur.assistenz.emomusic.tarsosdsp;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import de.ur.assistenz.emomusic.tarsosdsp.features.TarsosDSPAudioProcessor;
import weka.core.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeatureExtractor {

    private static final String RELATION = "features";

    private int windowSize = 512;
    private int windowOverlap = 0;

    private List<TarsosDSPAudioProcessor> features = new ArrayList<>();

    public FeatureExtractor(int windowSize, int windowOverlap) {
        this.windowSize = windowSize;
        this.windowOverlap = windowOverlap;
    }

    public void addFeature(TarsosDSPAudioProcessor feature) {
        this.features.add(feature);
    }

    public void removeFeature(TarsosDSPAudioProcessor feature) {
        this.features.remove(feature);
    }

    public List<TarsosDSPAudioProcessor> getFeatures() {
        return features;
    }

    private int calculateFeatureCount() {
        int count = 0;
        for(TarsosDSPAudioProcessor feature : features) {
            count += feature.getFeatureDimenion();
        }
        return count;
    }

    private Attribute createAttribute(TarsosDSPAudioProcessor feature, int index) {
        return new Attribute(feature.getFeatureName(index));
    }

    public FastVector createFeatureVectorDefinition(Attribute classAttribute) {
        FastVector definitionVector = new FastVector(calculateFeatureCount() + 1);
        definitionVector.addElement(classAttribute);
        for(TarsosDSPAudioProcessor feature : features) {
            float[] values = feature.getFeatureValue();
            for(int i = 0; i < values.length; i++) {
                definitionVector.addElement(createAttribute(feature, i));
            }
        }
        return definitionVector;
    }

    public Instance extract(File file, FastVector featureVectorDefinition) throws IOException, UnsupportedAudioFileException {
        createAudioDispatcher(file).run();
        Instance instance = new SparseInstance(calculateFeatureCount() + 1);
        for(TarsosDSPAudioProcessor feature : features) {
            float[] values = feature.getFeatureValue();
            for(int i = 0; i < values.length; i++) {
                Attribute attribute = createAttribute(feature, i);
                instance.setValue(attribute, values[i]);
            }
        }

        this.createFeatureVectorDefinition(null);

        if(featureVectorDefinition != null) {
            Instances dataSet = new Instances(RELATION, featureVectorDefinition, 1);
            dataSet.add(instance);
            dataSet.setClassIndex(0);

            return dataSet.firstInstance();
        }
        else {
            return instance;
        }
    }

    public void extract(File file) {
        try {
            extract(file, null);
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    private AudioDispatcher createAudioDispatcher(File file) throws IOException, UnsupportedAudioFileException {
        DefaultsAudioInputStream stream = new DefaultsAudioInputStream(file);
        AudioDispatcher dispatcher = new AudioDispatcher(stream, windowSize, windowOverlap);
        for(TarsosDSPAudioProcessor feature : this.features) {
            feature.setup(dispatcher.getFormat().getSampleRate(), windowSize, windowOverlap);
            dispatcher.addAudioProcessor(feature);
        }
        return dispatcher;
    }

    private class DefaultsAudioInputStream implements TarsosDSPAudioInputStream {

        private AudioInputStream underlyingStream;

        public DefaultsAudioInputStream(File file) throws IOException, UnsupportedAudioFileException {
            this.underlyingStream = AudioSystem.getAudioInputStream(file);
        }

        @Override
        public long skip(long l) throws IOException {
            return underlyingStream.skip(l);
        }

        @Override
        public int read(byte[] bytes, int i, int i1) throws IOException {
            return underlyingStream.read(bytes, i, i1);
        }

        @Override
        public void close() throws IOException {
            underlyingStream.close();
        }

        @Override
        public TarsosDSPAudioFormat getFormat() {
            AudioFormat format = this.underlyingStream.getFormat();
            float sampleRate = format.getSampleRate();
            int sampleSize = format.getSampleSizeInBits();
            if(sampleSize == AudioSystem.NOT_SPECIFIED) {
                sampleSize = 16;
            }
            int channels = format.getChannels();
            boolean isSigned = format.getEncoding() != AudioFormat.Encoding.PCM_UNSIGNED;
            boolean isBigEndian = format.isBigEndian();
            return new TarsosDSPAudioFormat(sampleRate, sampleSize, channels, isSigned, isBigEndian);
        }

        @Override
        public long getFrameLength() {
            long frameLength = this.underlyingStream.getFrameLength();
            if (frameLength == AudioSystem.NOT_SPECIFIED) {
                frameLength = 144;
            }
            return frameLength;
        }
    }

}
