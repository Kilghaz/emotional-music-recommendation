package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Listener.PlayerControlsObserver;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class PlayerControlsView extends HBox {

    private Button buttonPlay = new Button();
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

    public void updateProgressSliderPosition(Duration currentTime, Duration maxTime) {
        if(this.progressSlider.isValueChanging()) return;
        this.progressSlider.setMax(maxTime.toMillis());
        this.progressSlider.setValue(currentTime.toMillis());
    }

    public void onPlay() {
        this.buttonPlay.getStyleClass().removeAll("btn-play", "btn-pause");
        this.buttonPlay.getStyleClass().addAll("button", "btn-pause");
    }

    public void onPause() {
        this.buttonPlay.getStyleClass().removeAll("btn-play", "btn-pause");
        this.buttonPlay.getStyleClass().addAll("button", "btn-play");
    }

}
