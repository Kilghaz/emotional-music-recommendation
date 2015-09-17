package de.ur.assistenz.emomusic.classifier;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TrainingDataLoader {

    private List<String> trainingDataRaw;
    private List<String> headers;
    private List<HashMap<String, String>> instances;

    public void read(String path) throws IOException {
        this.trainingDataRaw = Files.readAllLines(Paths.get(path), Charset.defaultCharset());
        this.headers = readHeader();
        this.instances = readValues();
    }

    private List<String> readHeader() {
        List<String> headers = new ArrayList<>();
        Collections.addAll(headers, trainingDataRaw.get(0).split(";"));
        return headers;
    }

    private List<HashMap<String, String>> readValues() {
        List<HashMap<String, String>> instances = new ArrayList<>();
        for(int i = 1; i < trainingDataRaw.size(); i++) {
            HashMap<String, String> instance = new HashMap<>();
            String[] values = trainingDataRaw.get(i).split(";");
            for(int j = 0; j < values.length; j++) {
                instance.put(headers.get(j), values[j]);
            }
            instances.add(instance);
        }
        return instances;
    }

    public List<HashMap<String, String>> getInstances() {
        return this.instances;
    }

}
