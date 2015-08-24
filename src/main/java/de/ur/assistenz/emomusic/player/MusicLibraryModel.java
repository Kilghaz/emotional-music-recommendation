package de.ur.assistenz.emomusic.player;

import de.hijacksoft.oosql.DerbyAdapter;
import de.ur.assistenz.emomusic.DatabaseAdapterProvider;
import de.ur.assistenz.emomusic.SettingsManager;
import de.ur.assistenz.emomusic.player.Listener.MusicLibraryModelObserver;
import de.ur.assistenz.emomusic.sql.Song;
import javafx.application.Platform;

import java.io.File;
import java.util.List;

public class MusicLibraryModel {

    private static final String SETTING_LIBRARY_FOLDER = "library_folder";

    private static MusicLibraryModel instance = null;
    private DerbyAdapter derby;
    private SettingsManager settings = SettingsManager.getInstance();
    private MusicLibraryModelObserver observer = new NullObserver();

    private MusicLibraryModel(){
        this.derby = DatabaseAdapterProvider.getInstance().getAdapter();
        initializeDatabase();
        instance = this;
    }

    public static synchronized MusicLibraryModel getInstance() {
        return instance == null ? new MusicLibraryModel() : instance;
    }

    private void initializeDatabase() {
        if(!derby.doesTableExist("library")) {
            derby.createTable(Song.class);
        }
    }

    public void setLibraryFolder(File file) {
        settings.saveText(SETTING_LIBRARY_FOLDER, file.getAbsolutePath());
    }

    public void scan() {
        new ScanThread().start();
    }

    public void deleteAll() {
        fetchSongs().forEach(this::deleteSong);
    }

    public List<Song> fetchSongs() {
        List<Song> songs =  derby.select(Song.class);
        return songs;
    }

    public void addSong(Song song) {
        derby.insert(song);
        Platform.runLater(() -> observer.onSongAdded(song));
    }

    public void deleteSong(Song song) {
        derby.delete(song);
        Platform.runLater(() -> observer.onSongDeleted(song));
    }

    public void updateSong(Song song) {
        derby.update(song);
        Platform.runLater(() -> observer.onSongUpdated(song));
    }

    public void setObserver(MusicLibraryModelObserver observer) {
        this.observer = observer;
    }

    private class ScanThread extends Thread implements Runnable {

        @Override
        public void run() {
            Platform.runLater(observer::onScanStarted);
            String libraryFolderURL = settings.loadText(SETTING_LIBRARY_FOLDER);
            if(libraryFolderURL == null) return;
            File folder = new File(libraryFolderURL);
            File[] files = folder.listFiles((dir, name) -> name.endsWith("mp3") || name.endsWith("wav") || name.endsWith("m4a"));
            for (File file : files) {
                Song song = new Song();
                song.setName(file.getName());
                song.setUrl(file.getAbsolutePath());
                addSong(song);
            }
            Platform.runLater(observer::onScanFinished);
        }

    }

    private class NullObserver implements MusicLibraryModelObserver {

        @Override
        public void onScanFinished() {}

        @Override
        public void onScanStarted() {}

        @Override
        public void onSongAdded(Song song) {}

        @Override
        public void onSongDeleted(Song song) {}

        @Override
        public void onSongUpdated(Song song) {}

    }
}
