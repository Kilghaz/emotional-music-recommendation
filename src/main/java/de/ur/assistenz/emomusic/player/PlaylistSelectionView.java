package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Observer.Event;
import de.ur.assistenz.emomusic.player.Observer.EventReceiver;
import de.ur.assistenz.emomusic.player.Observer.EventSender;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class PlaylistSelectionView extends VBox {

    public static final String BUTTON_LIBRARY = "library";
    public static final String BUTTON_HAPPY = "happy";
    public static final String BUTTON_ANGRY = "angry";
    public static final String BUTTON_SAD = "sad";
    public static final String BUTTON_CALM = "calm";

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

    public void setActiveButton(String emotion) {
        buttonLibrary.getStyleClass().remove("active");
        buttonAngry.getStyleClass().remove("active");
        buttonSad.getStyleClass().remove("active");
        buttonCalm.getStyleClass().remove("active");
        buttonHappy.getStyleClass().remove("active");
        switch (emotion) {
            case BUTTON_LIBRARY: buttonLibrary.getStyleClass().add("active"); break;
            case BUTTON_ANGRY:   buttonAngry.getStyleClass().add("active"); break;
            case BUTTON_SAD:     buttonSad.getStyleClass().add("active"); break;
            case BUTTON_CALM:    buttonCalm.getStyleClass().add("active"); break;
            case BUTTON_HAPPY:   buttonHappy.getStyleClass().add("active"); break;
        }
    }

    private void initGUI() {
        buttonLibrary = new Button();
        buttonHappy = new Button();
        buttonAngry = new Button();
        buttonSad = new Button();
        buttonCalm = new Button();

        this.getChildren().add(buttonLibrary);
        this.getChildren().add(buttonHappy);
        this.getChildren().add(buttonAngry);
        this.getChildren().add(buttonSad);
        this.getChildren().add(buttonCalm);

        this.buttonLibrary.getStyleClass().addAll("button", "btn-filter", "btn-library");
        this.buttonLibrary.setPrefHeight(124);
        this.buttonLibrary.setPrefWidth(125);
        this.buttonLibrary.setMinWidth(125);
        this.buttonHappy.getStyleClass().addAll("button", "btn-filter", "btn-happy");
        this.buttonHappy.setPrefHeight(124);
        this.buttonHappy.setPrefWidth(125);
        this.buttonHappy.setMinWidth(125);
        this.buttonAngry.getStyleClass().addAll("button", "btn-filter", "btn-angry");
        this.buttonAngry.setPrefHeight(124);
        this.buttonAngry.setPrefWidth(125);
        this.buttonAngry.setMinWidth(125);
        this.buttonSad.getStyleClass().addAll("button", "btn-filter", "btn-sad");
        this.buttonSad.setPrefHeight(124);
        this.buttonSad.setPrefWidth(125);
        this.buttonSad.setMinWidth(125);
        this.buttonCalm.getStyleClass().addAll("button", "btn-filter", "btn-calm");
        this.buttonCalm.setPrefHeight(124);
        this.buttonCalm.setPrefWidth(125);
        this.buttonCalm.setMinWidth(125);

        this.buttonLibrary.setOnAction(actionEvent -> eventSender.notify(EVENT_LIBRARY_CLICKED));
        this.buttonHappy.setOnAction(actionEvent -> eventSender.notify(EVENT_HAPPY_CLICKED));
        this.buttonAngry.setOnAction(actionEvent -> eventSender.notify(EVENT_ANGRY_CLICKED));
        this.buttonSad.setOnAction(actionEvent -> eventSender.notify(EVENT_SAD_CLICKED));
        this.buttonCalm.setOnAction(actionEvent -> eventSender.notify(EVENT_CALM_CLICKED));
    }
}
