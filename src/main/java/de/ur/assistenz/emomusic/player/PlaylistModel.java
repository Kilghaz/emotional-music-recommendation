package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Observer.Event;
import de.ur.assistenz.emomusic.player.Observer.EventReceiver;
import de.ur.assistenz.emomusic.player.Observer.EventSender;
import de.ur.assistenz.emomusic.sql.Song;

import java.util.ArrayList;
import java.util.List;

public class PlaylistModel {

    private static final String EVENT_SONG_CHANGED = "song_changed";
    private static final String EVENT_LIST_CHANGED = "list_changed";

    private List<Song> songs = new ArrayList<>();
    private int currentSong = 0;
    private EventSender<Event> eventSender = new EventSender<>();

    public PlaylistModel() {
        eventSender.register(EVENT_SONG_CHANGED);
        eventSender.register(EVENT_LIST_CHANGED);
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void onSongChanged(EventReceiver receiver) {
        this.eventSender.on(EVENT_SONG_CHANGED, receiver);
    }

    public void onListChanged(EventReceiver receiver) {
        this.eventSender.on(EVENT_LIST_CHANGED, receiver);
    }

    public void shuffle() {
        // TODO: implement shuffle
    }

    public void repeat() {
        // TODO: implement repeat modes (single, list)
    }

    public Song getCurrentSong() {
        return songs.get(currentSong);
    }

    public int getCurrentSongIndex() {
        return currentSong;
    }

    public void next() {
        if(currentSong + 1 >= songs.size()) return;
        currentSong += 1;
        eventSender.notify(EVENT_SONG_CHANGED);
    }

    public void previous() {
        if(currentSong - 1 < 0) return;
        currentSong -= 1;
        eventSender.notify(EVENT_SONG_CHANGED);
    }

    public void setCurrentSong(int index) {
        if(index < 0 || index >= songs.size()) return;
        currentSong = index;
        eventSender.notify(EVENT_SONG_CHANGED);
    }

    public void setCurrentSong(Song song) {
        int index = songs.indexOf(song);
        if (index > 0) {
            this.currentSong = index;
            eventSender.notify(EVENT_SONG_CHANGED);
        }
    }

    public void addSong(Song song) {
        this.songs.add(song);
        eventSender.notify(EVENT_LIST_CHANGED);
    }

    public void removeSong(Song song) {
        this.songs.remove(song);
        eventSender.notify(EVENT_LIST_CHANGED);
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
        eventSender.notify(EVENT_LIST_CHANGED);
    }

}
