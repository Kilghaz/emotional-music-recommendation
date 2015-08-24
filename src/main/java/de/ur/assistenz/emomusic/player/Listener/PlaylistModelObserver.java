package de.ur.assistenz.emomusic.player.Listener;

import de.ur.assistenz.emomusic.sql.Song;

import java.util.List;

public interface PlaylistModelObserver {

    void onPlaylistChanged(List<Song> playlist);
    void onSongChanged(int index, Song song);

}
