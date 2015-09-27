package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.sql.Song;

public interface SongFilter {

    boolean filter(Song song);

}
