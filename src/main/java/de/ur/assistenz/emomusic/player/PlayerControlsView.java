package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Observer.Event;
import de.ur.assistenz.emomusic.player.Observer.EventReceiver;
import de.ur.assistenz.emomusic.player.Observer.EventSender;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class PlayerControlsView extends HBox {

    private static final String EVENT_SEEKER_MOVED = "seeker_moved";
    private static final String EVENT_PLAY_CLICKED = "sender";

    private Button buttonPlay = new Button();
    private Slider progressSlider = new Slider();

    private EventSender<Event> eventSender = new EventSender<>();

    public PlayerControlsView(){
        this.initGUI();
        this.getStyleClass().add("controls");
        this.eventSender.register(EVENT_PLAY_CLICKED);
        this.eventSender.register(EVENT_SEEKER_MOVED);
    }

    public void onPlayClicked(EventReceiver receiver) {
        this.eventSender.on(EVENT_PLAY_CLICKED, receiver);
    }

    public void onSeekerMoved(EventReceiver receiver) {
        this.eventSender.on(EVENT_SEEKER_MOVED, receiver);
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

        this.buttonPlay.setOnAction(actionEvent -> eventSender.notify(EVENT_PLAY_CLICKED));
        this.progressSlider.valueProperty().addListener((observableValue, number, t1) -> {
            if (this.progressSlider.isPressed()) {
                eventSender.notify(EVENT_SEEKER_MOVED);
            }
        });
    }

    public double getSeekerValue() {
        return progressSlider.getValue();
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
