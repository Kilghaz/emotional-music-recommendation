package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Listener.PlayerControlsObserver;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;

public class PlayerControlsView extends HBox {

    private Button buttonPlay = new Button("Play");
    private Slider progressSlider = new Slider();

    public PlayerControlsView(){
        this.initGUI();
        this.getStyleClass().add("controls");
    }

    public void setObserver(PlayerControlsObserver observer) {
        this.buttonPlay.setOnAction(observer::onPlayButtonClicked);
        this.progressSlider.setOnDragDone(observer::onProgressBarDrag);
    }

    private void initGUI() {
        this.buttonPlay.getStyleClass().addAll("button", "btn-play");
        this.buttonPlay.setPrefHeight(80);
        this.buttonPlay.setPrefWidth(80);
        this.buttonPlay.setMinWidth(80);

        this.progressSlider.getStyleClass().add("progress-slider");
        this.progressSlider.setPrefWidth(100000);

        this.getChildren().add(buttonPlay);
        this.getChildren().add(progressSlider);
    }

    public void onPlay() {
        this.buttonPlay.getStyleClass().removeAll("btn-play", "btn-pause");
        this.buttonPlay.getStyleClass().addAll("button", "btn-pause");
        this.buttonPlay.setText("Pause");
    }

    public void onPause() {
        this.buttonPlay.getStyleClass().removeAll("btn-play", "btn-pause");
        this.buttonPlay.getStyleClass().addAll("button", "btn-play");
        this.buttonPlay.setText("Play");
    }

}
