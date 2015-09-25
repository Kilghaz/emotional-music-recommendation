package de.ur.assistenz.emomusic.test;

import de.ur.assistenz.emomusic.classifier.EmotionClassifier;
import de.ur.assistenz.emomusic.jaudio.*;
import jAudioFeatureExtractor.AudioFeatures.MFCC;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

public class JAudioFeatureProcessorTest {

    private File testFileWav = new File("test-resources/test_audio.wav");
    private File testFileMp3 = new File("test-resources/test_audio.mp3");
    private File testFileMp32 = new File("test-resources/test_audio_2.mp3");

    EmotionClassifier emotionClassifier = new EmotionClassifier();

    @Test
    public void testExtractFeatures() throws Exception {
        calculateOverallAverageMFCC(testFileWav);
        assertArraysNotEquals(calculateOverallAverageMFCC(testFileMp3), calculateOverallAverageMFCC(testFileMp32));
    }

    @Test
    public void testCurrentProcessor() throws Exception {
        JAudioFeatureProcessor processor = emotionClassifier.createFeatureProcessor();
        processor.extractFeatures(testFileMp3);
        for(JAudioFeatureExtractor extractor : processor.getFeatures()) {
            System.out.println(extractor.getName());
            System.out.println(Arrays.toString(extractor.getAggreatedFeatureValues()));
        }
    }

    private double[] calculateOverallAverageMFCC(File file) throws Exception {
        JAudioFeatureProcessor featureProcessor = new JAudioFeatureProcessor(512, 0);
        featureProcessor.addFeatureExtractor(new JAudioFeatureExtractor(new MFCC()));
        featureProcessor.extractFeatures(file);
        JAudioFeatureAggregator overallAverage = new JAudioInverseOverallAverageAggregator();
        double[] overallAverageMFCC = overallAverage.aggregate(featureProcessor.getFeatures().get(0));
        System.out.println(Arrays.toString(overallAverageMFCC));
        return overallAverageMFCC;
    }

    private void assertArraysNotEquals(double[] a, double[] b) {
        assert a.length == b.length;
        for(int i = 0; i < a.length; i++) {
            assert a[i] != b[i];
        }
    }

}