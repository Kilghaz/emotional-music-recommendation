package de.ur.assistenz.emomusic.test;

import de.ur.assistenz.emomusic.classifier.FeatureExtractorFacade;
import jAudioFeatureExtractor.AudioFeatures.MFCC;
import jAudioFeatureExtractor.AudioFeatures.PowerSpectrum;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class FeatureExtractorFacadeTest {

    double samplingRate = 16.0;
    double windowOverlap = 0.0;
    int windowSize = 512;

    File audioFile = new File("Alice Cooper - Poison.mp3");

    @Test
    public void featureExtractionDimensionTest() {
        FeatureExtractorFacade powerSpectrum = new FeatureExtractorFacade(windowSize, windowOverlap, samplingRate, new PowerSpectrum());
        FeatureExtractorFacade mfcc = new FeatureExtractorFacade(windowSize, windowOverlap, samplingRate, new MFCC());
        Assert.assertEquals(windowSize / 2, powerSpectrum.extract(audioFile)[0].length);
        Assert.assertEquals(13, mfcc.extract(audioFile)[0].length);
    }

    @Test
    public void featureExtractionDependencyTest() {
        FeatureExtractorFacade mfcc = new FeatureExtractorFacade(windowSize, windowOverlap, samplingRate, new MFCC());
        Assert.assertNotNull(mfcc.extract(audioFile));
    }

}