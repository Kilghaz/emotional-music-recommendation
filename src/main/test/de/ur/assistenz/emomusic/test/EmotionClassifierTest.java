package de.ur.assistenz.emomusic.test;

import de.ur.assistenz.emomusic.classifier.EmotionClassifier;
import de.ur.assistenz.emomusic.classifier.XMLTrainingDataLoader;
import org.junit.Test;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Debug;
import weka.core.Instances;

import java.io.File;

public class EmotionClassifierTest {

    @Test
    public void testClassify() throws Exception {
        EmotionClassifier classifier = new EmotionClassifier();
        String result = classifier.classify(new File("test-resources/test_audio.mp3"));
        assert result != null;
    }

    @Test
    public void evaluateClassification() throws Exception {
        EmotionClassifier classifier = new EmotionClassifier();
        XMLTrainingDataLoader loader = new XMLTrainingDataLoader();
        Instances trainingSet = loader.load(new File("training_data.xml"), 0.9f, classifier.createFeatureExtractorInstance().getFeatures());
        Evaluation eval = new Evaluation(trainingSet);
        eval.crossValidateModel(new NaiveBayes(), trainingSet, 10, new Debug.Random(1));
        System.out.println(eval.toSummaryString(false));
    }

}