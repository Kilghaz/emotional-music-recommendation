package de.ur.assistenz.emomusic.classifier;

import org.openimaj.audio.SampleChunk;
import org.openimaj.audio.features.MFCC;
import org.openimaj.audio.features.SpectralFlux;
import org.openimaj.video.xuggle.XuggleAudio;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class EmotionClassifier {

    private Classifier classifier;

    private static EmotionClassifier instance = null;
    private FastVector featureVectorDefinition;

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
        definitionVector.addElement(new Attribute("mfcc_overall_average_13"));
        definitionVector.addElement(new Attribute("spectral_flux_overall_average_0"));

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

        Instances trainingSet = new Instances("training_set", featureVectorDefinition, values.size());
        trainingSet.setClassIndex(0);

        for(HashMap<String, String> songFeatures : values) {
            Instance featureVector = new SparseInstance(featureVectorDefinition.capacity());
            featureVector.setValue((Attribute) featureVectorDefinition.elementAt(0), songFeatures.get("emotion"));
            for(int i = 1; i < featureVectorDefinition.capacity(); i++) {
                Attribute attr = (Attribute) featureVectorDefinition.elementAt(i);
                featureVector.setValue(attr, Double.parseDouble(songFeatures.get(attr.name())));
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

    private Instance extractFeatures(File audioFile) {
        XuggleAudio audio = new XuggleAudio(audioFile);

        double[] mfcc = calculateOverallAverageMFCC(audio);
        double spectralFlux = calculateOverallAverageSpectralFlux(audio);

        Instance instance = new SparseInstance(15);

        int mfccOffset = 1;

        for(int i = 0; i < mfcc.length; i++) {
            instance.setValue((Attribute)featureVectorDefinition.elementAt(i + mfccOffset), mfcc[i]);
        }
        instance.setValue((Attribute)featureVectorDefinition.elementAt(14), spectralFlux);

        return instance;
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
