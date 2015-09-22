package de.ur.assistenz.emomusic.classifier;

import de.ur.assistenz.emomusic.classifier.features.EmotionFeature;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class TrainingDataBuilder {

    public HashMap<File, String> loadAnnotations(File csvFile, String keyFilePath, String keyAnnotation) {
        HashMap<File, String> annotations = new HashMap<>();
        try {
            CSVDataLoader loader = new CSVDataLoader();
            loader.read(csvFile.getAbsolutePath());
            for(HashMap<String, String> instance : loader.getInstances()) {
                File file = new File(instance.get(keyFilePath));
                String annotation = instance.get(keyAnnotation);
                annotations.put(file, annotation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return annotations;
    }

    public String build(HashMap<File, String> annotatedFiles, FeatureExtractor extractor) {
        String csv = createHeader(extractor.getFeatures());
        for(File file : annotatedFiles.keySet()) {
            extractor.extract(file);
            csv += createRow(file, annotatedFiles.get(file), extractor.getFeatures());
        }
        return csv;
    }

    private String createHeader(List<EmotionFeature> features) {
        String csvHeader = "Filename;Annotation;";
        for(EmotionFeature feature : features) {
            for(int i = 0; i < feature.getFeatureDimenion(); i++) {
                csvHeader += feature.getFeatureName(i);
            }
        }
        return csvHeader + "\n";
    }

    private String createRow(File file, String annotation, List<EmotionFeature> features) {
        String csvRow = file.getAbsolutePath() + ";";
        csvRow += annotation + ";";
        for(EmotionFeature feature : features) {
            float[] values = feature.getFeatureValue();
            for(int i = 0; i < feature.getFeatureDimenion(); i++) {
                csvRow += values[i];
            }
        }
        return csvRow + "\n";
    }

}
