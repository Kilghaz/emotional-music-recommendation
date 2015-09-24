package de.ur.assistenz.emomusic.classifier;

import java.util.List;

public interface TrainingDataBuilder {

    String getSupportedFormat();
    String build(List<AnnotatedFile> annotatedFiles, FeatureExtractorFactory extractorFactory);

}
