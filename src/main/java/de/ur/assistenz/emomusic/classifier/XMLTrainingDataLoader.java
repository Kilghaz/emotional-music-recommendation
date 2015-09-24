package de.ur.assistenz.emomusic.classifier;

import de.ur.assistenz.emomusic.classifier.features.EmotionFeature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import weka.core.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XMLTrainingDataLoader {

    private static final String TAG_INSTANCE = "song";
    private static final String TAG_FEATURE = "feature";
    private static final String TAG_FEATURE_NAME = "name";
    private static final String TAG_FEATURE_VALUE = "value";
    private static final String TAG_KAPPA = "kappa";
    private static final String TAG_ANNOTATION = "annotation";

    public static final String RELATION = "music_emotion";
    public static final FastVector CLASS_VALUES;

    static {
        CLASS_VALUES = new FastVector(4);
        CLASS_VALUES.addElement("happy_amazed");
        CLASS_VALUES.addElement("sad_lonely");
        CLASS_VALUES.addElement("angry");
        CLASS_VALUES.addElement("calm_relaxing");
    }

    private FastVector featureVectorDefinition;

    public Instances load(File file, float kappaThreshold, List<EmotionFeature> usedFeatures) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        NodeList songs = document.getElementsByTagName(TAG_INSTANCE);

        featureVectorDefinition = createFeatureVectorDefinition((Element) songs.item(0), usedFeatures);
        Instances instances = new Instances(RELATION, featureVectorDefinition, songs.getLength());
        instances.setClassIndex(0);

        for(int i = 0; i< songs.getLength(); i++) {
            Element song = (Element) songs.item(i);
            float kappa = extractKappaValue(song);
            if (kappa < kappaThreshold)
                continue;
            String annotation = extractAnnotation(song);
            HashMap<String, Float> features = extractFeatures(song);
            instances.add(createInstance(annotation, features));
        }

        return instances;
    }

    private Instance createInstance(String annotation, HashMap<String, Float> features) {
        Instance instance = new SparseInstance(featureVectorDefinition.size());
        instance.setValue((Attribute) featureVectorDefinition.elementAt(0), annotation);
        for(int i = 1; i < featureVectorDefinition.size(); i++) {
            Attribute attribute = (Attribute) featureVectorDefinition.elementAt(i);
            instance.setValue(attribute, features.get(attribute.name()));
        }
        return instance;
    }

    private HashMap<String, Float> extractFeatures(Element songElement) {
        HashMap<String, Float> featureMap = new HashMap<>();
        NodeList features = songElement.getElementsByTagName(TAG_FEATURE);
        for(int i = 0; i < features.getLength(); i++) {
            Element feature = (Element) features.item(i);
            String name = feature.getElementsByTagName(TAG_FEATURE_NAME).item(0).getTextContent();
            float value = Float.parseFloat(feature.getElementsByTagName(TAG_FEATURE_VALUE).item(0).getTextContent());
            featureMap.put(name, value);
        }
        return featureMap;
    }

    private float extractKappaValue(Element songElement) {
        return Float.parseFloat(songElement.getElementsByTagName(TAG_KAPPA).item(0).getTextContent());
    }

    private String extractAnnotation(Element songElement) {
        NodeList annotations = songElement.getElementsByTagName(TAG_ANNOTATION);
        HashMap<String, Integer> annotationCounts = new HashMap<>();
        for(int i = 0; i < annotations.getLength(); i++){
            String annotation = annotations.item(i).getTextContent();
            Integer count = annotationCounts.get(annotation);
            if(count == null) {
                count = 0;
            }
            annotationCounts.put(annotation, ++count);
        }
        int maxCount = 0;
        String result = null;
        for(String annotation : annotationCounts.keySet()) {
            if(maxCount < annotationCounts.get(annotation) || result == null) {
                result = annotation;
            }
        }
        return result;
    }

    private List<Element> filterUsedFeatures(NodeList features, List<EmotionFeature> usedFeatures) {
        List<Element> nodes = new ArrayList<>();
        for (int i = 0; i < features.getLength(); i++) {
            Element feature = (Element) features.item(i);
            String featureName = feature.getElementsByTagName(TAG_FEATURE_NAME).item(0).getTextContent();
            for(EmotionFeature usedFeature : usedFeatures){
                if(featureName.startsWith(usedFeature.getFeatureName())){
                    nodes.add(feature);
                }
            }
        }
        return nodes;
    }

    private FastVector createFeatureVectorDefinition(Element songElement, List<EmotionFeature> usedFeatures) {
        List<Element> features = filterUsedFeatures(songElement.getElementsByTagName(TAG_FEATURE), usedFeatures);
        FastVector featureVectorDefinition = new FastVector(features.size() + 1);
        featureVectorDefinition.addElement(new Attribute("emotion", CLASS_VALUES));
        for (Element feature : features) {
            String featureName = feature.getElementsByTagName(TAG_FEATURE_NAME).item(0).getTextContent();
            featureVectorDefinition.addElement(new Attribute(featureName));
        }
        return featureVectorDefinition;
    }

    public FastVector getFeatureVectorDefinition() {
        return featureVectorDefinition;
    }
}
