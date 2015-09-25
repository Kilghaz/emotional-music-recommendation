package de.ur.assistenz.emomusic.jaudio;

import jAudioFeatureExtractor.AudioFeatures.*;

import java.util.HashMap;

public class JAudioFeatureExtractorFactory {

    private static final HashMap<String, Class<? extends FeatureExtractor>> FEATURE_EXTRACTOR_CLASSES;

    static {
        FEATURE_EXTRACTOR_CLASSES = new HashMap<>();
        FEATURE_EXTRACTOR_CLASSES.put("Area Method of Moments", AreaMoments.class);
        FEATURE_EXTRACTOR_CLASSES.put("Area Method of Moments Beat Histogram", AreaMomentsBeatHistogram.class);
        FEATURE_EXTRACTOR_CLASSES.put("Area Method of Moments of ConstantQ-based MFCCs", AreaMomentsConstantQMFCC.class);
        FEATURE_EXTRACTOR_CLASSES.put("Area Method of Moments of Log of ConstantQ transform", AreaMomentsLogConstantQ.class);
        FEATURE_EXTRACTOR_CLASSES.put("Area Method of Moments of MFCCs", AreaMomentsMFCC.class);
        FEATURE_EXTRACTOR_CLASSES.put("2D Polynomial Approximation", AreaPolynomialApproximation.class);
        FEATURE_EXTRACTOR_CLASSES.put("2D Polynomial Approximation ConstantQ MFCC", AreaPolynomialApproximationConstantQMFCC.class);
        FEATURE_EXTRACTOR_CLASSES.put("2D Polynomial Approximation of Log of ConstantQ", AreaPolynomialApproximationLogConstantQ.class);
        FEATURE_EXTRACTOR_CLASSES.put("Beat Histogram", BeatHistogram.class);
        FEATURE_EXTRACTOR_CLASSES.put("Beat Histogram Bin Labels", BeatHistogramLabels.class);
        FEATURE_EXTRACTOR_CLASSES.put("Beat Sum", BeatSum.class);
        FEATURE_EXTRACTOR_CLASSES.put("Chroma", Chroma.class);
        FEATURE_EXTRACTOR_CLASSES.put("Compactness", Compactness.class);
        FEATURE_EXTRACTOR_CLASSES.put("ConstantQ", ConstantQ.class);
        FEATURE_EXTRACTOR_CLASSES.put("ConstantQ derived MFCCs", ConstantQMFCC.class);
        FEATURE_EXTRACTOR_CLASSES.put("FFT Bin Frequency Labels", FFTBinFrequencies.class);
        FEATURE_EXTRACTOR_CLASSES.put("Fraction Of Low Energy Windows", FractionOfLowEnergyWindows.class);
        FEATURE_EXTRACTOR_CLASSES.put("Partial Based Spectral Centroid", HarmonicSpectralCentroid.class);
        FEATURE_EXTRACTOR_CLASSES.put("Partial Based Spectral Flux", HarmonicSpectralFlux.class);
        FEATURE_EXTRACTOR_CLASSES.put("Peak Based Spectral Smoothness", HarmonicSpectralSmoothness.class);
        FEATURE_EXTRACTOR_CLASSES.put("LPC", LPC.class);
        FEATURE_EXTRACTOR_CLASSES.put("LPC (alt)", LPCRemoved.class);
        FEATURE_EXTRACTOR_CLASSES.put("Log of ConstantQ", LogConstantQ.class);
        FEATURE_EXTRACTOR_CLASSES.put("MFCC", MFCC.class);
        FEATURE_EXTRACTOR_CLASSES.put("Magnitude Spectrum", MagnitudeSpectrum.class);
        FEATURE_EXTRACTOR_CLASSES.put("Method of Moments", Moments.class);
        FEATURE_EXTRACTOR_CLASSES.put("Peak Detection", PeakFinder.class);
        FEATURE_EXTRACTOR_CLASSES.put("Power Spectrum", PowerSpectrum.class);
        FEATURE_EXTRACTOR_CLASSES.put("Root Mean Square", RMS.class);
        FEATURE_EXTRACTOR_CLASSES.put("Relative Difference Function", RelativeDifferenceFunction.class);
        FEATURE_EXTRACTOR_CLASSES.put("Spectral Centroid", SpectralCentroid.class);
        FEATURE_EXTRACTOR_CLASSES.put("Spectral Flux", SpectralFlux.class);
        FEATURE_EXTRACTOR_CLASSES.put("Spectral Rolloff Point", SpectralRolloffPoint.class);
        FEATURE_EXTRACTOR_CLASSES.put("Spectral Variability", SpectralVariability.class);
        FEATURE_EXTRACTOR_CLASSES.put("Strength Of Strongest Beat", StrengthOfStrongestBeat.class);
        FEATURE_EXTRACTOR_CLASSES.put("Strongest Beat", StrongestBeat.class);
        FEATURE_EXTRACTOR_CLASSES.put("Strongest Frequency Variability", StrongestFrequencyVariability.class);
        FEATURE_EXTRACTOR_CLASSES.put("Strongest Frequency Via FFT Maximum", StrongestFrequencyViaFFTMax.class);
        FEATURE_EXTRACTOR_CLASSES.put("Strongest Frequency Via Spectral Centroid", StrongestFrequencyViaSpectralCentroid.class);
        FEATURE_EXTRACTOR_CLASSES.put("Strongest Frequency Via Zero Crossings", StrongestFrequencyViaZeroCrossings.class);
        FEATURE_EXTRACTOR_CLASSES.put(" Traditional Area Method of Moments", TraditionalAreaMoments.class);
        FEATURE_EXTRACTOR_CLASSES.put("Zernike Moments", ZernikeMoments.class);
        FEATURE_EXTRACTOR_CLASSES.put("Zernike Moments Beat Histogram", ZernikeMomentsBeatHistogram.class);
        FEATURE_EXTRACTOR_CLASSES.put("Zero Crossings", ZeroCrossings.class);
    }

    public FeatureExtractor createFeatureExtractor(String name) {
        Class<? extends FeatureExtractor> cls = FEATURE_EXTRACTOR_CLASSES.get(name);
        if(cls == null) return null;
        try {
            return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
