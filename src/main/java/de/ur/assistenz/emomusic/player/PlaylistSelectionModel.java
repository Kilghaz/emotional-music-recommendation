package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Observer.Event;
import de.ur.assistenz.emomusic.player.Observer.EventReceiver;
import de.ur.assistenz.emomusic.player.Observer.EventSender;
import de.ur.assistenz.emomusic.sql.Song;

import java.util.HashMap;

public class PlaylistSelectionModel {

    public static final String PLAYLIST_LIBRARY = "library";
    public static final String PLAYLIST_ANGRY = "angry";
    public static final String PLAYLIST_SAD = "sad";
    public static final String PLAYLIST_CALM = "calm";
    public static final String PLAYLIST_HAPPY = "happy";
    private static final String EVENT_PLAYLIST_CHANGED = "playlist_changed";

    private String currentPlaylist = PLAYLIST_LIBRARY;
    private HashMap<String, SongFilter> playlistFilter = new HashMap<>();

    private EventSender<Event> eventSender = new EventSender<>();

    public PlaylistSelectionModel() {
        playlistFilter.put(PLAYLIST_LIBRARY, (song) -> true);
        playlistFilter.put(PLAYLIST_ANGRY, (song) -> song.getEmotion().equals(Song.EMOTION_ANRGY));
        playlistFilter.put(PLAYLIST_SAD, (song) -> song.getEmotion().equals(Song.EMOTION_SAD));
        playlistFilter.put(PLAYLIST_CALM, (song) -> song.getEmotion().equals(Song.EMOTION_CALM));
        playlistFilter.put(PLAYLIST_HAPPY, (song) -> song.getEmotion().equals(Song.EMOTION_HAPPY));
        eventSender.register(EVENT_PLAYLIST_CHANGED);
    }

    public SongFilter getCurrentPlaylistFilter() {
        SongFilter filter = playlistFilter.get(currentPlaylist);
        if(filter == null) {
            filter = playlistFilter.get(PLAYLIST_LIBRARY);
        }
        return filter;
    }

    public String getPlaylist() {
        return this.currentPlaylist;
    }

    public void setPlaylist(String playlist) {
        this.currentPlaylist = playlist;
        eventSender.notify(EVENT_PLAYLIST_CHANGED);
    }

    public void onPlaylistChanged(EventReceiver receiver) {
        eventSender.on(EVENT_PLAYLIST_CHANGED, receiver);
    }

}
