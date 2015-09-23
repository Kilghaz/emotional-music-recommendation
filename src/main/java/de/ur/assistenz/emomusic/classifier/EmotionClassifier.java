package de.ur.assistenz.emomusic.classifier;

import de.ur.assistenz.emomusic.classifier.features.*;
import org.xml.sax.SAXException;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class EmotionClassifier {

    private static final int WINDOW_SIZE = 512;
    private static final int WINDOW_OVERLAP = 0;
    private static final float KAPPA_THRESHOLD = 0.50f;

    private static EmotionClassifier instance = null;

    private Classifier classifier;
    private FeatureExtractor featureExtractor;
    private XMLTrainingDataLoader loader = new XMLTrainingDataLoader();

    public EmotionClassifier() {
        instance = this;
        this.featureExtractor = createFeatureExtractor();
        train();
    }

    public static synchronized EmotionClassifier getInstance() {
        return instance == null ? new EmotionClassifier() : instance;
    }

    private void train() {
        Instances trainingSet = null;

        try {
            trainingSet = loader.load(new File("training_data.xml"), KAPPA_THRESHOLD);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        this.classifier = new NaiveBayes(); // TODO: change to better classifier maybe (NaiveBayesUpdateable)
        try {
            this.classifier.buildClassifier(trainingSet);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private FeatureExtractor createFeatureExtractor() {
        FeatureExtractor featureExtractor = new FeatureExtractor(WINDOW_SIZE, WINDOW_OVERLAP);
        featureExtractor.addFeature(new OverallAverageMFCC(13, 100, 10000));
        featureExtractor.addFeature(new OverallAverageRMS());
        featureExtractor.addFeature(new OverallStandardDeviationMFCC(13, 100, 10000));
        featureExtractor.addFeature(new OverallStandardDeviationRMS());
        featureExtractor.addFeature(new DominantPitches(5));
        return featureExtractor;
    }

    private Instance extractFeatures(File audioFile) {
        featureExtractor.extract(audioFile);
        FastVector featureVectorDefinition = loader.getFeatureVectorDefinition();

        Instance instance = new SparseInstance(featureVectorDefinition.size());

        for(EmotionFeature feature : featureExtractor.getFeatures()) {
            float[] values = feature.getFeatureValue();
            for(int i = 0; i < values.length; i++) {
                instance.setValue(new Attribute(feature.getFeatureName(i)), values[i]);
            }
        }

        Instances dataSet = new Instances(XMLTrainingDataLoader.RELATION, featureVectorDefinition, 1);
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

}
