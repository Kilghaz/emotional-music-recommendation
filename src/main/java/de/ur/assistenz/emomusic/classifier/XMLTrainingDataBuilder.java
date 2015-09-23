package de.ur.assistenz.emomusic.classifier;

import de.ur.assistenz.emomusic.classifier.features.EmotionFeature;

import java.util.List;

public class XMLTrainingDataBuilder implements TrainingDataBuilder {

    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private FleissKappaCalculator<String> kappaCalculator = new FleissKappaCalculator<>();

    private int indentationLevel = 0;

    @Override
    public String getSupportedFormat() {
        return "xml";
    }

    @Override
    public String build(List<AnnotatedFile> annotatedFiles, FeatureExtractor extractor) {
        String xml = XML_HEADER + "\n";
        indentationLevel = 0;
        xml += "<songs>\n";
        indentationLevel++;
        double current = 0;
        for(AnnotatedFile annotatedFile : annotatedFiles) {
            extractor.extract(annotatedFile.getFile());
            xml += renderSong(annotatedFile.getAnnotations(), extractor.getFeatures());
            current += 1.0;
            System.out.println("extracting features: " + current / annotatedFiles.size());
        }
        xml += "</songs>\n";
        return xml;
    }

    private String renderSong(List<String> annotations, List<EmotionFeature> features) {
        String xml = "";
        xml += indentation() + "<song>\n"; indentationLevel++;
        xml += indentation() + "<annotations>\n"; indentationLevel++;
        xml += indentation() + "<kappa>" + kappaCalculator.calculate(annotations, annotations.size()) + "</kappa>\n";
        for(String annotation : annotations) {
            xml += indentation() + "<annotation>" + annotation + "</annotation>\n";
        }
        indentationLevel--;
        xml += indentation() + "</annotations>\n";
        xml += indentation() + "<features>\n"; indentationLevel++;
        for(EmotionFeature feature : features) {
            float[] values = feature.getFeatureValue();
            for(int i = 0; i < feature.getFeatureDimenion(); i++) {
                xml += indentation() + "<feature>\n"; indentationLevel++;
                xml += indentation() + "<name>"  + feature.getFeatureName(i) + "</name>\n";
                xml += indentation() + "<value>" + values[i] + "</value>\n"; indentationLevel--;
                xml += indentation() + "</feature>\n";
            }
        }
        indentationLevel--;
        xml += indentation() + "</features>\n"; indentationLevel--;
        xml += indentation() + "</song>\n";
        return xml;
    }

    private String indentation() {
        return indentation(indentationLevel);
    }

    private String indentation(int level) {
        String indentation = "";
        for(int i = 0; i < level; i++) {
            indentation += "\t";
        }
        return indentation;
    }

}
