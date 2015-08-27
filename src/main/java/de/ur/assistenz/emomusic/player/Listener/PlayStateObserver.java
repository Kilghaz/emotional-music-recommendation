package de.ur.assistenz.emomusic.player.Listener;

import javafx.beans.Observable;

public interface PlayStateObserver {

    void onPlay();
    void onPause();
    void onStop();
    void onProgress(Observable observable);

}
