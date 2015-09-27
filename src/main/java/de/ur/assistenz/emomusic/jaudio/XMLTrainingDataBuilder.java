package de.ur.assistenz.emomusic.jaudio;

import de.ur.assistenz.emomusic.classifier.AnnotatedFile;
import de.ur.assistenz.emomusic.classifier.FleissKappaCalculator;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class XMLTrainingDataBuilder {

    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private FleissKappaCalculator<String> kappaCalculator = new FleissKappaCalculator<>();
    private static final int THREAD_COUNT = 2;

    private int indentationLevel = 0;
    private double progress = 0;
    private double maxProgress = 0;
    private CountDownLatch countDownLatch;
    private List<AnnotatedFile> annotatedFiles;

    private StringBuilder xmlStringBuilder;

    public String build(List<AnnotatedFile> annotatedFiles, JAudioFeatureProcessorFactory factory) {
        indentationLevel = 0;
        indentationLevel++;
        progress = 0;
        maxProgress = annotatedFiles.size();
        countDownLatch = new CountDownLatch(THREAD_COUNT);
        xmlStringBuilder = new StringBuilder();
        xmlStringBuilder.append(XML_HEADER + "\n");
        xmlStringBuilder.append("<songs>\n");
        this.annotatedFiles = annotatedFiles;
        for(int i = 0; i < THREAD_COUNT; i++) {
            new ExtractionThread(factory.createFeatureProcessor()).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        xmlStringBuilder.append("</songs>\n");
        return xmlStringBuilder.toString();
    }

    private String renderSong(List<String> annotations, List<JAudioFeatureExtractor> features) {
        StringBuilder xml = new StringBuilder();
        xml.append(indentation()).append("<song>\n");
        indentationLevel++;
        xml.append(indentation()).append("<annotations>\n");
        indentationLevel++;
        xml.append(indentation()).append("<kappa>").append(kappaCalculator.calculate(annotations, annotations.size())).append("</kappa>\n");
        for(String annotation : annotations) {
            xml.append(indentation()).append("<annotation>").append(annotation).append("</annotation>\n");
        }
        indentationLevel--;
        xml.append(indentation()).append("</annotations>\n");
        xml.append(indentation()).append("<features>\n");
        indentationLevel++;
        for(JAudioFeatureExtractor feature : features) {
            double[] values = feature.getAggreatedFeatureValues();
            for(int i = 0; i < values.length; i++) {
                xml.append(indentation()).append("<feature>\n");
                indentationLevel++;
                xml.append(indentation()).append("<name>").append(feature.getName(i)).append("</name>\n");
                xml.append(indentation()).append("<value>").append(values[i]).append("</value>\n");
                indentationLevel--;
                xml.append(indentation()).append("</feature>\n");
            }
        }
        indentationLevel--;
        xml.append(indentation()).append("</features>\n");
        indentationLevel--;
        xml.append(indentation()).append("</song>\n");
        return xml.toString();
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

    private synchronized AnnotatedFile getNextAnnotatedFile() {
        if(annotatedFiles.size() == 0) {
            return null;
        }
        return annotatedFiles.remove(0);
    }

    private synchronized void appendString(String string) {
        this.xmlStringBuilder.append(string);
    }

    private void printProgress() {
        double progressPercent = ((double)(int)(progress/maxProgress * 10000))/100;
        System.out.println("extracting features: " + progressPercent + "%");
    }

    private class ExtractionThread extends Thread implements Runnable {

        private JAudioFeatureProcessor processor;

        public ExtractionThread(JAudioFeatureProcessor processor) {
            this.processor = processor;
        }

        @Override
        public void run() {
            AnnotatedFile annotatedFile;
            while ((annotatedFile = getNextAnnotatedFile()) != null) {
                processor.extractFeatures(annotatedFile.getFile());
                appendString(renderSong(annotatedFile.getAnnotations(), processor.getFeatures()));
                progress += 1;
                printProgress();
            }
            countDownLatch.countDown();
        }
    }

}
