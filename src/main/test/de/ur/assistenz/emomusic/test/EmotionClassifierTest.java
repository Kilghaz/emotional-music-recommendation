package de.ur.assistenz.emomusic.test;

import de.ur.assistenz.emomusic.classifier.EmotionClassifier;
import org.junit.Test;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.ClassificationViaRegression;
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
        Instances trainingSet = classifier.getTrainingData();
        Evaluation eval = new Evaluation(trainingSet);
        eval.crossValidateModel(new ClassificationViaRegression(), trainingSet, 25, new Debug.Random(10));
        System.out.println(eval.toSummaryString(false));
    }

}