package de.ur.assistenz.emomusic;

import de.ur.assistenz.emomusic.classifier.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateTrainingData {

    public CreateTrainingData(String filePath, String csv, String out) throws Exception {
        EmotionClassifier classifier = new EmotionClassifier();
        File[] files = new File(filePath).listFiles();
        List<AnnotatedFile> annotatedFiles = createAnnotatedFiles(files, csv);
        XMLTrainingDataBuilder xmlTrainingDataBuilder = new XMLTrainingDataBuilder();
        String xml = xmlTrainingDataBuilder.build(annotatedFiles, classifier);
        writeXML(xml, new File(out));
    }

    private void writeXML(String xml, File file) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter printWriter = new PrintWriter(file, "UTF-8");
        printWriter.print(xml);
        printWriter.close();
    }

    private List<AnnotatedFile> createAnnotatedFiles(File[] files, String csv) throws IOException {
        List<AnnotatedFile> annotatedFiles = new ArrayList<>();
        CSVDataLoader csvDataLoader = new CSVDataLoader();
        csvDataLoader.read(csv);
        List<HashMap<String, String>> instances = csvDataLoader.getInstances();
        double current = 0;
        for(HashMap<String, String> instance : instances) {
            List<String> annotations = new ArrayList<>();
            File file = bestMatchingFile(files, instance.get("id"));
            annotations.add(instance.get("annotation_0"));
            annotations.add(instance.get("annotation_1"));
            annotations.add(instance.get("annotation_2"));
            annotatedFiles.add(new AnnotatedFile(file, annotations));
            current += 1.0;
            System.out.println("reading csv: " + calculateProgress(current, instances.size()) + "%");
        }
        return annotatedFiles;
    }

    private double calculateProgress(double current, double max) {
        return ((double)(int)(current/max * 10000))/100;
    }

    private File bestMatchingFile(File[] files, String filename) {
        int minDistance = Integer.MAX_VALUE;
        File bestFile = null;
        for(File file : files) {
            int distance = levenshteinDistance(file.getName(), filename);
            if(distance < minDistance || bestFile == null) {
                bestFile = file;
                minDistance = distance;
            }
        }
        return bestFile;
    }

    public int levenshteinDistance(String lhs, String rhs) {
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;
        int[] cost = new int[len0];
        int[] newcost = new int[len0];
        for (int i = 0; i < len0; i++) cost[i] = i;
        for (int j = 1; j < len1; j++) {
            newcost[0] = j;
            for(int i = 1; i < len0; i++) {
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;
                int cost_replace = cost[i - 1] + match;
                int cost_insert  = cost[i] + 1;
                int cost_delete  = newcost[i - 1] + 1;
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }
            int[] swap = cost; cost = newcost; newcost = swap;
        }
        return cost[len0 - 1];
    }

    public static void main(String[] args) throws Exception {
        new CreateTrainingData(args[1], args[0], args[2]);
    }

}
