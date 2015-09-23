package de.ur.assistenz.emomusic.test;

import de.ur.assistenz.emomusic.classifier.EmotionClassifier;
import org.junit.Test;

import java.io.File;

public class EmotionClassifierTest {

    @Test
    public void testClassify() throws Exception {
        EmotionClassifier classifier = new EmotionClassifier();
        String result = classifier.classify(new File("test-resources/test_audio.mp3"));
        System.out.println(result);
    }

}