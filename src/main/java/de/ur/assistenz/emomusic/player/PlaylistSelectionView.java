package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Observer.Event;
import de.ur.assistenz.emomusic.player.Observer.EventReceiver;
import de.ur.assistenz.emomusic.player.Observer.EventSender;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * Created by Jenny on 20.09.2015.
 */
public class PlaylistSelectionView extends VBox {

    private Button buttonLibrary;
    private Button buttonHappy;
    private Button buttonAngry;
    private Button buttonSad;
    private Button buttonCalm;

    private static final String EVENT_LIBRARY_CLICKED = "library";
    private static final String EVENT_HAPPY_CLICKED = "happy";
    private static final String EVENT_ANGRY_CLICKED = "angry";
    private static final String EVENT_SAD_CLICKED = "sad";
    private static final String EVENT_CALM_CLICKED = "calm";

    private EventSender<Event> eventSender = new EventSender<>();

    public PlaylistSelectionView() {
        initGUI();
        this.eventSender.register(EVENT_LIBRARY_CLICKED);
        this.eventSender.register(EVENT_HAPPY_CLICKED);
        this.eventSender.register(EVENT_ANGRY_CLICKED);
        this.eventSender.register(EVENT_SAD_CLICKED);
        this.eventSender.register(EVENT_CALM_CLICKED);
    }

    public void onLibraryClicked(EventReceiver receiver) {
        this.eventSender.on(EVENT_LIBRARY_CLICKED, receiver);
    }
    public void onHappyPlayClicked(EventReceiver receiver) {
        this.eventSender.on(EVENT_HAPPY_CLICKED, receiver);
    }
    public void onAngryClicked(EventReceiver receiver) {
        this.eventSender.on(EVENT_ANGRY_CLICKED, receiver);
    }
    public void onSadClicked(EventReceiver receiver) {
        this.eventSender.on(EVENT_SAD_CLICKED, receiver);
    }
    public void onCalmClicked(EventReceiver receiver) {
        this.eventSender.on(EVENT_CALM_CLICKED, receiver);
    }



    private void initGUI() {

        buttonLibrary = new Button("Library");
        buttonHappy = new Button("Happy");
        buttonAngry = new Button("Angry");
        buttonSad = new Button("Sad");
        buttonCalm = new Button("Calm");
        this.getChildren().add(buttonLibrary);
        this.getChildren().add(buttonHappy);
        this.getChildren().add(buttonAngry);
        this.getChildren().add(buttonSad);
        this.getChildren().add(buttonCalm);

        this.buttonLibrary.getStyleClass().addAll("button", "btn-filter");
        this.buttonLibrary.setPrefHeight(100);
        this.buttonLibrary.setPrefWidth(100);
        this.buttonLibrary.setMinWidth(100);
        this.buttonHappy.getStyleClass().addAll("button", "btn-filter");
        this.buttonHappy.setPrefHeight(100);
        this.buttonHappy.setPrefWidth(100);
        this.buttonHappy.setMinWidth(100);
        this.buttonAngry.getStyleClass().addAll("button", "btn-filter");
        this.buttonAngry.setPrefHeight(100);
        this.buttonAngry.setPrefWidth(100);
        this.buttonAngry.setMinWidth(100);
        this.buttonSad.getStyleClass().addAll("button", "btn-filter");
        this.buttonSad.setPrefHeight(100);
        this.buttonSad.setPrefWidth(100);
        this.buttonSad.setMinWidth(100);
        this.buttonCalm.getStyleClass().addAll("button", "btn-filter");
        this.buttonCalm.setPrefHeight(100);
        this.buttonCalm.setPrefWidth(100);
        this.buttonCalm.setMinWidth(100);

        this.buttonLibrary.setOnAction(actionEvent -> eventSender.notify(EVENT_LIBRARY_CLICKED));
        this.buttonHappy.setOnAction(actionEvent -> eventSender.notify(EVENT_HAPPY_CLICKED));
        this.buttonAngry.setOnAction(actionEvent -> eventSender.notify(EVENT_ANGRY_CLICKED));
        this.buttonSad.setOnAction(actionEvent -> eventSender.notify(EVENT_SAD_CLICKED));
        this.buttonCalm.setOnAction(actionEvent -> eventSender.notify(EVENT_CALM_CLICKED));
    }
}
