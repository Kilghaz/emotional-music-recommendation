package de.ur.assistenz.emomusic.player.Listener;

public interface AudioPlayerObserver extends PlayStateObserver {

    void onSongFinished();
    void onPlayError();

}
