package de.ur.assistenz.emomusic.player.Observer;

import de.ur.assistenz.emomusic.sql.Song;

public class SongEvent extends Event {

    public SongEvent(Song song) {
        put("song", song);
    }

    public Song getSong() {
        return (Song) get("song");
    }

}
