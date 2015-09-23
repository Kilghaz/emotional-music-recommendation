package de.ur.assistenz.emomusic.classifier;

import java.io.File;
import java.util.List;

public class AnnotatedFile {

    private File file;
    private List<String> annotations;
    private float kappa;

    public AnnotatedFile(File file, List<String> annotations) {
        this.file = file;
        this.annotations = annotations;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<String> annotations) {
        this.annotations = annotations;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    private float calculateKappa() {
        return 0;
    }

}