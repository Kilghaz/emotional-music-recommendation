package de.ur.assistenz.emomusic.player.Observer;

import java.io.File;

public class FileEvent extends Event {

    public FileEvent(File file) {
        put("file", file);
    }

    public File getFile() {
        return (File) get("file");
    }

}
