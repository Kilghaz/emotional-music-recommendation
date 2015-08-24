package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Listener.PlaylistModelObserver;
import de.ur.assistenz.emomusic.sql.Song;

import java.util.ArrayList;
import java.util.List;

public class PlaylistModel {

    private List<Song> songs = new ArrayList<>();
    private int currentSong = 0;
    private PlaylistModelObserver playlistModelObserver = new NullObserver();

    public void setObserver(PlaylistModelObserver playlistModelObserver){
        this.playlistModelObserver = playlistModelObserver;
    }

    public void shuffle() {
        // TODO: implement shuffle
    }

    public void repeat() {
        // TODO: implement repeat modes (single, list)
    }

    public void next() {
        if(currentSong + 1 >= songs.size()) return;
        currentSong += 1;
        playlistModelObserver.onSongChanged(currentSong, songs.get(currentSong));
    }

    public void previous() {
        if(currentSong - 1 < 0) return;
        currentSong -= 1;
        playlistModelObserver.onSongChanged(currentSong, songs.get(currentSong));
    }

    public void selectSong(int index) {
        if(index < 0 || index >= songs.size()) return;
        currentSong = index;
        playlistModelObserver.onSongChanged(currentSong, songs.get(currentSong));
    }

    public void addSong(Song song) {
        this.songs.add(song);
        playlistModelObserver.onPlaylistChanged(this.songs);
    }

    public void removeSong(Song song) {
        this.songs.remove(song);
        playlistModelObserver.onPlaylistChanged(this.songs);
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
        playlistModelObserver.onPlaylistChanged(this.songs);
    }

    private class NullObserver implements PlaylistModelObserver {

        @Override
        public void onPlaylistChanged(List<Song> playlist) {}

        @Override
        public void onSongChanged(int index, Song song) {}

    }

}
