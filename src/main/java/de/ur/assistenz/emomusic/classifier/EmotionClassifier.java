package de.ur.assistenz.emomusic.classifier;

import de.ur.assistenz.emomusic.tarsosdsp.FeatureExtractor;
import de.ur.assistenz.emomusic.tarsosdsp.FeatureExtractorFactory;
import de.ur.assistenz.emomusic.tarsosdsp.features.*;
import de.ur.assistenz.emomusic.jaudio.*;
import de.ur.assistenz.emomusic.jaudio.XMLTrainingDataLoader;
import jAudioFeatureExtractor.AudioFeatures.Chroma;
import jAudioFeatureExtractor.AudioFeatures.MFCC;
import jAudioFeatureExtractor.AudioFeatures.RMS;
import org.xml.sax.SAXException;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class EmotionClassifier implements FeatureExtractorFactory, JAudioFeatureProcessorFactory {

    private static final int WINDOW_SIZE = 441;
    private static final int WINDOW_OVERLAP = 100;
    private static final float KAPPA_THRESHOLD = 0.90f;

    private static final File TRAINING_DATA = new File("training_data.xml");

    private static EmotionClassifier instance = null;

    private Classifier classifier;
    private XMLTrainingDataLoader loader = new XMLTrainingDataLoader();
    private Instances trainingData;

    public EmotionClassifier() {
        instance = this;
        train();
    }

    public static synchronized EmotionClassifier getInstance() {
        return instance == null ? new EmotionClassifier() : instance;
    }

    private void train() {
        trainingData = null;

        try {
            trainingData = loader.load(TRAINING_DATA, KAPPA_THRESHOLD, createFeatureProcessor().getFeatures());
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        this.classifier = new NaiveBayes(); // TODO: change to better classifier maybe (NaiveBayesUpdateable)
        try {
            this.classifier.buildClassifier(trainingData);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Instance extractFeatures(File audioFile) {
        JAudioFeatureProcessor featureProcessor = this.createFeatureProcessor();
        featureProcessor.extractFeatures(audioFile);
        FastVector featureVectorDefinition = loader.getFeatureVectorDefinition();

        Instance instance = new SparseInstance(featureVectorDefinition.size());
        instance.setDataset(trainingData);

        for(int i = 1; i < instance.numAttributes(); i++) {
            Attribute attribute = instance.attribute(i);
            instance.setValue(attribute, featureProcessor.getFeatureValue(attribute.name()));
        }

        return instance;
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

    @Override
    public FeatureExtractor createFeatureExtractorInstance() {
        FeatureExtractor featureExtractor = new FeatureExtractor(WINDOW_SIZE, WINDOW_OVERLAP);
        featureExtractor.addFeature(new OverallAverageMFCC(13, 133.3334f, 22000f));
        featureExtractor.addFeature(new OverallStandardDeviationMFCC(13, 133.3334f, 22000f));
        featureExtractor.addFeature(new OverallAverageRMS());
        featureExtractor.addFeature(new OverallStandardDeviationRMS());
        featureExtractor.addFeature(new OverallAveragePitch());
        featureExtractor.addFeature(new OverallStandardDeviationPitch());
        return featureExtractor;
    }

    @Override
    public JAudioFeatureProcessor createFeatureProcessor() {
        JAudioFeatureProcessor processor = new JAudioFeatureProcessor(WINDOW_SIZE, WINDOW_OVERLAP);
        processor.addFeatureExtractor(new MFCC(), new JAudioInverseOverallAverageAggregator());
        processor.addFeatureExtractor(new MFCC(), new JAudioOverallStandardDeviationAggregaotor());
        processor.addFeatureExtractor(new RMS());
        processor.addFeatureExtractor(new Chroma());
        return processor;
    }

    public Instances getTrainingData() {
        return trainingData;
    }

}
