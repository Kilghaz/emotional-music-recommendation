package de.ur.assistenz.emomusic;

import de.hijacksoft.oosql.DerbyAdapter;
import de.ur.assistenz.emomusic.sql.Song;

import java.util.List;

public class MusicLibraryModel {

    private static MusicLibraryModel instance = null;
    private DerbyAdapter derby;

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

    public List<Song> fetchSongs() {
        return derby.select(Song.class);
    }

    public void addSong(Song song) {
        derby.insert(song);
    }

    public void deleteSong(Song song) {
        derby.delete(song);
    }

    public void updateSong(Song song) {
        derby.update(song);
    }

}
