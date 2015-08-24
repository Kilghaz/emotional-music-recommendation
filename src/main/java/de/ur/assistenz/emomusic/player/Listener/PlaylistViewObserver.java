package de.ur.assistenz.emomusic.player.Listener;

import de.ur.assistenz.emomusic.sql.Song;

public interface PlaylistViewObserver {

    void onSongSelected(int index, Song song);

}