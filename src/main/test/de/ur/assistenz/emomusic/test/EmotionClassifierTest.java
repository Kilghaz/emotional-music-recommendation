package de.ur.assistenz.emomusic.test;

import de.ur.assistenz.emomusic.classifier.EmotionClassifier;
import org.junit.Test;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Debug;
import weka.core.Instance;
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
        Instances trainingSet = classifier.getTrainingData();
        Evaluation eval = new Evaluation(trainingSet);
        eval.crossValidateModel(new NaiveBayes(), trainingSet, 10, new Debug.Random(10));
        System.out.println(eval.toSummaryString(false));
    }

    @Test
    public void evaluateInstances() throws Exception {
        Classifier nb = new NaiveBayes();
        EmotionClassifier classifier = new EmotionClassifier();
        Instances trainingSet = classifier.getTrainingData();
        nb.buildClassifier(trainingSet);
        for(int i = 0; i < trainingSet.numInstances(); i++) {
            Instance instance = trainingSet.instance(i);
            System.out.println(nb.classifyInstance(instance));
        }
        Instance instance = trainingSet.instance(2);
        for(int i = 0; i < instance.numAttributes(); i++) {
            System.out.println(instance.attribute(i) + " = " + instance.value(i));
        }
    }

}