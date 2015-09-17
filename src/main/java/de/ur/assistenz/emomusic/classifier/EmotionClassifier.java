package de.ur.assistenz.emomusic.classifier;

import org.openimaj.audio.SampleChunk;
import org.openimaj.audio.features.MFCC;
import org.openimaj.audio.features.SpectralFlux;
import org.openimaj.video.xuggle.XuggleAudio;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmotionClassifier {

    private Classifier classifier;

    private static EmotionClassifier instance = null;

    public EmotionClassifier() {
        instance = this;
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
        definitionVector.addElement(new Attribute("mfcc_1"));
        definitionVector.addElement(new Attribute("mfcc_2"));
        definitionVector.addElement(new Attribute("mfcc_3"));
        definitionVector.addElement(new Attribute("mfcc_4"));
        definitionVector.addElement(new Attribute("mfcc_5"));
        definitionVector.addElement(new Attribute("mfcc_6"));
        definitionVector.addElement(new Attribute("mfcc_7"));
        definitionVector.addElement(new Attribute("mfcc_8"));
        definitionVector.addElement(new Attribute("mfcc_9"));
        definitionVector.addElement(new Attribute("mfcc_10"));
        definitionVector.addElement(new Attribute("mfcc_11"));
        definitionVector.addElement(new Attribute("mfcc_12"));
        definitionVector.addElement(new Attribute("mfcc_13"));
        definitionVector.addElement(new Attribute("spectral_flux"));

        // TODO: add more features

        return definitionVector;
    }

    private void train() {
        List<HashMap<String, Object>> values = new ArrayList<>();
        // TODO: READ CSV
        FastVector definitionVector = createFeatureVectorDefinition();
        Instances trainingSet = new Instances("training_set", definitionVector, values.size());
        trainingSet.setClassIndex(0);

        for(HashMap<String, Object> songFeatures : values) {
            Instance featureVector = new SparseInstance(definitionVector.capacity());
            featureVector.setValue((Attribute) definitionVector.elementAt(0), (String) songFeatures.get("emotion"));
            for(int i = 1; i < definitionVector.capacity(); i++) {
                Attribute attr = (Attribute) definitionVector.elementAt(i);
                featureVector.setValue(attr, (Double) songFeatures.get(attr.name()));
            }
            trainingSet.add(featureVector);
        }

        this.classifier = new NaiveBayes(); // TODO: change to better classifier maybe
        try {
            this.classifier.buildClassifier(trainingSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Instance extractFeatures(File audioFile) {
        XuggleAudio audio = new XuggleAudio(audioFile);

        double[] mfcc = calculateOverallAverageMFCC(audio);
        double spectralFlux = calculateOverallAverageSpectralFlux(audio);

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
            System.out.println(overallAverageMFCC[i]);
        }

        return overallAverageMFCC;
    }

    public String classify(File audioFile) {
        Instance instance = extractFeatures(audioFile);
        try {
            assert instance != null;
            int emotion = (int) Math.round(this.classifier.classifyInstance(instance));
            return new String[]{
                    "happy_amazed",
                    "sad_lonely",
                    "angry",
                    "calm_relaxing"
            }[emotion];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
