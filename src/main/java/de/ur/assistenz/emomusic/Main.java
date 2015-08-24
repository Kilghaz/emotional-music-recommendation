package de.ur.assistenz.emomusic;

import de.ur.assistenz.emomusic.player.PlayerWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public void start(Stage primaryStage) {
        PlayerWindow window = new PlayerWindow(primaryStage);
    }

}
