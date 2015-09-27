package de.ur.assistenz.emomusic.classifier;

import de.ur.assistenz.emomusic.tarsosdsp.FeatureExtractorFactory;

import java.util.List;

public interface TrainingDataBuilder {

    String getSupportedFormat();
    String build(List<AnnotatedFile> annotatedFiles, FeatureExtractorFactory extractorFactory);

}
