package de.ur.assistenz.emomusic.player;

import de.hijacksoft.oosql.DerbyAdapter;
import de.ur.assistenz.emomusic.DatabaseAdapterProvider;
import de.ur.assistenz.emomusic.SettingsManager;
import de.ur.assistenz.emomusic.player.Observer.EventReceiver;
import de.ur.assistenz.emomusic.player.Observer.EventSender;
import de.ur.assistenz.emomusic.player.Observer.SongEvent;
import de.ur.assistenz.emomusic.sql.Song;
import javafx.application.Platform;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.util.List;

public class MusicLibraryModel {

    private static final String SETTING_LIBRARY_FOLDER = "library_folder";

    private static final String EVENT_SCAN_STARTED = "scan_started";
    private static final String EVENT_SCAN_FINISHED = "scan_started";
    private static final String EVENT_SONG_UPDATED = "song_update";
    private static final String EVENT_SONG_ADDED = "song_add";
    private static final String EVENT_SONG_DELETED = "song_delete";

    private static MusicLibraryModel instance = null;
    private DerbyAdapter derby;
    private SettingsManager settings = SettingsManager.getInstance();
    private EventSender<SongEvent> eventSender = new EventSender<>();
    // private EmotionClassifier classifier = new EmotionClassifier();

    private MusicLibraryModel(){
        this.derby = DatabaseAdapterProvider.getInstance().getAdapter();
        initializeDatabase();
        this.eventSender.register(EVENT_SCAN_FINISHED);
        this.eventSender.register(EVENT_SCAN_STARTED);
        this.eventSender.register(EVENT_SONG_UPDATED);
        this.eventSender.register(EVENT_SONG_ADDED);
        this.eventSender.register(EVENT_SONG_DELETED);
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

    public void onSongAdded(EventReceiver<SongEvent> eventReceiver) {
        eventSender.on(EVENT_SONG_ADDED, eventReceiver);
    }

    public void onSongDeleted(EventReceiver<SongEvent> eventReceiver) {
        eventSender.on(EVENT_SONG_DELETED, eventReceiver);
    }

    public void onSongUpdated(EventReceiver<SongEvent> eventReceiver) {
        eventSender.on(EVENT_SONG_UPDATED, eventReceiver);
    }

    public void onScanStarted(EventReceiver receiver) {
        eventSender.on(EVENT_SCAN_STARTED, receiver);
    }

    public void onScanFinished(EventReceiver receiver) {
        eventSender.on(EVENT_SCAN_FINISHED, receiver);
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

    public void addSong(File file) {
        Song song = new Song();
        song.setName(file.getName());
        song.setUrl(file.getAbsolutePath());
        // song.setEmotion(classifier.classify(file));
        Tag tag = readSongMeta(file);
        if(tag != null) {
            String name = tag.getFirst(FieldKey.TITLE);
            song.setName(name == null ? song.getName() : name);
            song.setArtist(tag.getFirst(FieldKey.ARTIST));
            song.setAlbum(tag.getFirst(FieldKey.ALBUM));
            song.setYear(tag.getFirst(FieldKey.YEAR));
        }
        addSong(song);
    }

    public void addSong(Song song) {
        derby.insert(song);
        Platform.runLater(() -> eventSender.notify(EVENT_SONG_ADDED, new SongEvent(song)));
    }

    public void deleteSong(Song song) {
        derby.delete(song);
        Platform.runLater(() -> eventSender.notify(EVENT_SONG_DELETED, new SongEvent(song)));
    }

    public void updateSong(Song song) {
        derby.update(song);
        Platform.runLater(() -> eventSender.notify(EVENT_SONG_UPDATED, new SongEvent(song)));
    }

    private class ScanThread extends Thread implements Runnable {

        @Override
        public void run() {
            Platform.runLater(() -> eventSender.notify(EVENT_SCAN_STARTED));
            String libraryFolderURL = settings.loadText(SETTING_LIBRARY_FOLDER);
            if(libraryFolderURL == null) return;
            File folder = new File(libraryFolderURL);
            File[] files = folder.listFiles((dir, name) -> name.endsWith("mp3") || name.endsWith("wav") || name.endsWith("m4a"));
            for (File file : files) {
                addSong(file);
            }
            Platform.runLater(() -> eventSender.notify(EVENT_SCAN_FINISHED));
        }

    }

    private Tag readSongMeta(File file) {
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            return audioFile.getTag();
        } catch (Exception e) {
            return null;
        }
    }

}
