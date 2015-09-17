package de.ur.assistenz.emomusic;

import de.ur.assistenz.emomusic.classifier.EmotionClassifier;

import java.io.File;

public class Test {

    public static void main(String[] args) {
        EmotionClassifier classifier = new EmotionClassifier();
        System.out.println(classifier.classify(new File("GP_P09.mp3")));
    }

}
