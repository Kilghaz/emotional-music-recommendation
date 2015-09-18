package de.ur.assistenz.emomusic.classifier;

import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;
import jAudioFeatureExtractor.AudioFeatures.PowerSpectrum;
import org.openimaj.audio.SampleChunk;
import org.openimaj.audio.features.MFCC;
import org.openimaj.audio.features.SpectralFlux;
import org.openimaj.video.xuggle.XuggleAudio;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmotionClassifier {

    private static final String RELATION = "music_emotion";

    private int windowSize = 512;   // samples
    private double windowOverlap = 0.0;
    private double samplingRate = 16.0;

    private static EmotionClassifier instance = null;

    private Classifier classifier;
    private FastVector featureVectorDefinition;

    private double kappaThreshold = 0.50;

    public EmotionClassifier() {
        instance = this;
        this.featureVectorDefinition = createFeatureVectorDefinition();
        train();
    }

    public static synchronized EmotionClassifier getInstance() {
        return instance == null ? new EmotionClassifier() : instance;
    }

    private FastVector createFeatureVectorDefinition() {
        FastVector emotionValues = new FastVector(4);
        emotionValues.addElement("happy_amazed");
        emotionValues.addElement("sad_lonely");
        emotionValues.addElement("angry");
        emotionValues.addElement("calm_relaxing");

        FastVector definitionVector = new FastVector(14);
        definitionVector.addElement(new Attribute("emotion", emotionValues));
        definitionVector.addElement(new Attribute("mfcc_overall_average_0"));
        definitionVector.addElement(new Attribute("mfcc_overall_average_1"));
        definitionVector.addElement(new Attribute("mfcc_overall_average_2"));
        definitionVector.addElement(new Attribute("mfcc_overall_average_3"));
        definitionVector.addElement(new Attribute("mfcc_overall_average_4"));
        definitionVector.addElement(new Attribute("mfcc_overall_average_5"));
        definitionVector.addElement(new Attribute("mfcc_overall_average_6"));
        definitionVector.addElement(new Attribute("mfcc_overall_average_7"));
        definitionVector.addElement(new Attribute("mfcc_overall_average_8"));
        definitionVector.addElement(new Attribute("mfcc_overall_average_9"));
        definitionVector.addElement(new Attribute("mfcc_overall_average_10"));
        definitionVector.addElement(new Attribute("mfcc_overall_average_11"));
        definitionVector.addElement(new Attribute("mfcc_overall_average_12"));

        // Spectral flux is not in the training data so we cannot use it for now.
        // definitionVector.addElement(new Attribute("spectral_flux_overall_average_0"));

        return definitionVector;
    }

    private void train() {
        TrainingDataLoader dataLoader = new TrainingDataLoader();
        try {
            dataLoader.read("training_data.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<HashMap<String, String>> values = dataLoader.getInstances();

        Instances trainingSet = new Instances(RELATION, featureVectorDefinition, values.size());
        trainingSet.setClassIndex(0);

        for(HashMap<String, String> songFeatures : values) {
            double kappa = getDoubleValue("fleiss_kappa_annotation", songFeatures);
            if(kappa < this.kappaThreshold) {
                continue;
            }
            Instance featureVector = new SparseInstance(featureVectorDefinition.capacity());
            featureVector.setValue((Attribute) featureVectorDefinition.elementAt(0), selectAnnotation(songFeatures));
            for(int i = 1; i < featureVectorDefinition.capacity(); i++) {
                Attribute attr = (Attribute) featureVectorDefinition.elementAt(i);
                featureVector.setValue(attr, getDoubleValue(attr.name(), songFeatures));
            }
            trainingSet.add(featureVector);
        }

        this.classifier = new NaiveBayes(); // TODO: change to better classifier maybe (NaiveBayesUpdateable)
        try {
            this.classifier.buildClassifier(trainingSet);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Double getDoubleValue(String key, HashMap<String, String> songFeatures) {
        return Double.parseDouble(songFeatures.get(key).replace(",", "."));
    }

    private String selectAnnotation(HashMap<String, String> instance) {
        List<String> annotations = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            annotations.add(instance.get("annotation_" + i));
        }
        String annotation = null;
        int maxCount = 0;
        for(String a : annotations) {
            int count = count(a, annotations);
            if(count > maxCount) {
                annotation = a;
            }
        }
        return annotation;
    }

    private int count(String needle, List<String> haystack) {
        int count = 0;
        for(String value : haystack) {
            if(value.equals(needle)) {
                count++;
            }
        }
        return count;
    }

    private Instance extractFeatures(File audioFile) {
        XuggleAudio audio = new XuggleAudio(audioFile);
        double[] mfcc = calculateOverallAverageMFCC(audio);

        Instance instance = new SparseInstance(15);

        int mfccOffset = 1;

        for(int i = 0; i < mfcc.length; i++) {
            instance.setValue((Attribute)featureVectorDefinition.elementAt(i + mfccOffset), mfcc[i]);
        }

        Instances dataSet = new Instances(RELATION, featureVectorDefinition, 1);
        dataSet.add(instance);
        dataSet.setClassIndex(0);

        return dataSet.firstInstance();
    }

    private double[] calculatePowerSpectrum(File audioFile) throws Exception {
        double[][][] featureValues = extractFeature(new PowerSpectrum(), audioFile);
        return null;
    }

    private double calculateOverallAverageSpectralFlux(XuggleAudio audio) {
        SpectralFlux spectralFlux = new SpectralFlux(audio);

        SampleChunk sc = null;

        double overallAverageSpectralFlux = 0;  // spectral flux has only 1 value
        int length = 0;

        while ((sc = spectralFlux.nextSampleChunk()) != null) {
            double[][] values = spectralFlux.getLastCalculatedFeature();
            for(double[] fluxValue : values) {
                overallAverageSpectralFlux += fluxValue[0];  // spectral flux has only 1 value
                length++;
            }
        }

        return overallAverageSpectralFlux / length;
    }

    private double[] calculateOverallAverageMFCC(XuggleAudio audio) {
        MFCC mfcc = new MFCC(audio);

        SampleChunk sc = null;

        double[] overallAverageMFCC = new double[13];   // MFCC always has 13 values
        int length = 0;

        while ((sc = mfcc.nextSampleChunk()) != null) {
            for(double[] mfccValues : mfcc.getLastCalculatedFeature()) {
                for(int i = 0; i < mfccValues.length; i++) {
                    overallAverageMFCC[i] += mfccValues[i];
                }
                length++;
            }
        }

        for(int i = 0; i < overallAverageMFCC.length; i++) {
            overallAverageMFCC[i] /= length;
        }

        return overallAverageMFCC;
    }

    public String classify(File audioFile) {
        Instance instance = extractFeatures(audioFile);
        try {
            assert instance != null;
            double result = this.classifier.classifyInstance(instance);
            return new String[]{
                    "happy_amazed",
                    "sad_lonely",
                    "angry",
                    "calm_relaxing"
            }[(int) Math.round(result)];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getKappaThreshold() {
        return kappaThreshold;
    }

    public void setKappaThreshold(double kappaThreshold) {
        this.kappaThreshold = kappaThreshold;
        train();
    }

    private double[][][] extractFeature(FeatureExtractor extractor, File audioFile) {
        try {
            SimpleFeatureProcessor processor = new SimpleFeatureProcessor(this.windowSize, this.windowOverlap, this.samplingRate, extractor);
            return processor.process(audioFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public double getWindowOverlap() {
        return windowOverlap;
    }

    public void setWindowOverlap(double windowOverlap) {
        this.windowOverlap = windowOverlap;
    }

    public double getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(double samplingRate) {
        this.samplingRate = samplingRate;
    }
}
