package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Listener.PlayerControlsObserver;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.HBox;

public class PlayerControlsView extends HBox {

    private Button buttonPlay = new Button();
    private Slider progressSlider = new Slider();

    private PlayerControlsObserver observer = new NullObserver();

    public PlayerControlsView(){
        this.initGUI();
    }

    public void setObserver(PlayerControlsObserver observer) {
        this.observer = observer;
    }

    private void initGUI() {
        this.buttonPlay.getStyleClass().addAll("button", "btn-play");
        this.progressSlider.getStyleClass().add("progress-slider");

        this.buttonPlay.setOnAction(observer::onPlayButtonClicked);
        this.progressSlider.setOnDragDone(observer::onProgressBarDrag);

        this.getChildren().add(buttonPlay);
        this.getChildren().add(progressSlider);
    }

    private class NullObserver implements PlayerControlsObserver {

        @Override
        public void onPlayButtonClicked(ActionEvent event) {}

        @Override
        public void onProgressBarDrag(DragEvent event) {}

    }

}
