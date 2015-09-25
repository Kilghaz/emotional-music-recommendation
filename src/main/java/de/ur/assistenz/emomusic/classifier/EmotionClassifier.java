package de.ur.assistenz.emomusic.classifier;

import de.ur.assistenz.emomusic.classifier.features.*;
import de.ur.assistenz.emomusic.jaudio.JAudioFeatureExtractor;
import de.ur.assistenz.emomusic.jaudio.JAudioFeatureProcessor;
import de.ur.assistenz.emomusic.jaudio.JAudioFeautreProcessorFactory;
import de.ur.assistenz.emomusic.jaudio.JAudioInverseOverallAverageAggregator;
import jAudioFeatureExtractor.AudioFeatures.MFCC;
import org.xml.sax.SAXException;
import weka.classifiers.Classifier;
import weka.classifiers.meta.ClassificationViaRegression;
import weka.core.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class EmotionClassifier implements FeatureExtractorFactory, JAudioFeautreProcessorFactory {

    private static final int WINDOW_SIZE = 441;
    private static final int WINDOW_OVERLAP = 100;
    private static final float KAPPA_THRESHOLD = 0.0f;

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
            trainingData = loader.load(TRAINING_DATA, KAPPA_THRESHOLD, createFeatureExtractorInstance().getFeatures());
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        this.classifier = new ClassificationViaRegression(); // TODO: change to better classifier maybe (NaiveBayesUpdateable)
        try {
            this.classifier.buildClassifier(trainingData);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Instance extractFeatures(File audioFile) {
        FeatureExtractor featureExtractor = this.createFeatureExtractorInstance();
        featureExtractor.extract(audioFile);
        FastVector featureVectorDefinition = loader.getFeatureVectorDefinition();

        Instance instance = new SparseInstance(featureVectorDefinition.size());

        for(EmotionFeature feature : featureExtractor.getFeatures()) {
            float[] values = feature.getFeatureValue();
            for(int i = 0; i < values.length; i++) {
                instance.setValue(new Attribute(feature.getFeatureName(i)), values[i]);
            }
        }

        Instances dataSet = trainingData;
        dataSet.add(instance);
        dataSet.setClassIndex(0);

        return dataSet.firstInstance();
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
        // using values from (McKinney et al., 2003)
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
        processor.addFeatureExtractor(new JAudioFeatureExtractor(new MFCC(), new JAudioInverseOverallAverageAggregator())); // Overall Average MFCC
        return processor;
    }

    public Instances getTrainingData() {
        return trainingData;
    }

}
