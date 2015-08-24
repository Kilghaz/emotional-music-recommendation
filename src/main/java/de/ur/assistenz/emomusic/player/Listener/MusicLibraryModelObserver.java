package de.ur.assistenz.emomusic.player.Listener;

import de.ur.assistenz.emomusic.sql.Song;

public interface MusicLibraryModelObserver {

    void onScanFinished();
    void onScanStarted();
    void onSongAdded(Song song);
    void onSongDeleted(Song song);
    void onSongUpdated(Song song);

}
