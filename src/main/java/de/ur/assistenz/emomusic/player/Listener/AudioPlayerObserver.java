package de.ur.assistenz.emomusic.player.Listener;

public interface AudioPlayerObserver {

    void onSongFinished();
    void onPlayError();

}
