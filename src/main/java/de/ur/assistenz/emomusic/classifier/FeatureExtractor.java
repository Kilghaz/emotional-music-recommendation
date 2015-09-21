package de.ur.assistenz.emomusic.classifier;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.mfcc.MFCC;
import weka.core.Instance;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeatureExtractor implements AudioProcessor {

    private int windowSize = 512;
    private int windowOverlap = 0;

    private AudioDispatcher dispatcher;
    private MFCC mfccProcessor;

    private int windowCount = 0;

    private List<float[]> mfccValues;
    private List<Double> rmsValues;

    private float[] overallAverageMFCC;
    private double overallAverageRMS;

    public FeatureExtractor(int windowSize, int windowOverlap) {
        this.windowSize = windowSize;
        this.windowOverlap = windowOverlap;
    }

    public Instance extract(File file) throws IOException, UnsupportedAudioFileException {
        initAudioDispatcher(file).run();
        return null;
    }

    private float getSampleRate() {
        return dispatcher.getFormat().getSampleRate();
    }

    private MFCC createMFCCAudioProcessor() {
        this.mfccProcessor = new MFCC(windowSize, (int) getSampleRate());
        return this.mfccProcessor;
    }

    private AudioDispatcher initAudioDispatcher(File file) throws IOException, UnsupportedAudioFileException {
        DefaultsAudioInputStream stream = new DefaultsAudioInputStream(file);
        this.dispatcher = new AudioDispatcher(stream, windowSize, windowOverlap);
        this.dispatcher.addAudioProcessor(createMFCCAudioProcessor());
        this.dispatcher.addAudioProcessor(this);
        this.mfccValues = new ArrayList<>();
        this.rmsValues = new ArrayList<>();
        this.windowCount = 0;
        return this.dispatcher;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        mfccValues.add(this.mfccProcessor.getMFCC());
        rmsValues.add(audioEvent.getRMS());
        this.windowCount++;
        return true;
    }

    private float[] calculateOverallAverageMFCC() {
        float[] overallAverageMFCC = new float[13];  // use only the first 13 mfccs
        for(int i = 0; i < overallAverageMFCC.length; i++) {
            overallAverageMFCC[i] = 0;
        }
        for(float[] values : mfccValues) {
            for(int i = 0; i < overallAverageMFCC.length; i++) {
                overallAverageMFCC[i] += values[i];
            }
        }
        for(int i = 0; i < overallAverageMFCC.length; i++) {
            overallAverageMFCC[i] /= this.windowCount;
        }
        return overallAverageMFCC;
    }

    private double calculateOverallAverageRMS() {
        float overallAverageRMS = 0;
        for(double value : this.rmsValues) {
            overallAverageRMS += value;
        }
        return overallAverageRMS / this.windowCount;
    }

    @Override
    public void processingFinished() {
        this.overallAverageMFCC = calculateOverallAverageMFCC();
        this.overallAverageRMS = calculateOverallAverageRMS();
    }

    public double getOverallAverageRMS() {
        return overallAverageRMS;
    }

    public float[] getOverallAverageMFCC() {
        return overallAverageMFCC;
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
            boolean isSigned = format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED;
            return new TarsosDSPAudioFormat(sampleRate, sampleSize, channels, isSigned, format.isBigEndian());
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
