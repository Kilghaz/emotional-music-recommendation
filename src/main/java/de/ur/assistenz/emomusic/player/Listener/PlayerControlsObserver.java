package de.ur.assistenz.emomusic.player.Listener;

import javafx.event.ActionEvent;
import javafx.scene.input.DragEvent;

public interface PlayerControlsObserver {

    void onPlayButtonClicked(ActionEvent event);
    void onProgressBarValueChanged(double value);

}
