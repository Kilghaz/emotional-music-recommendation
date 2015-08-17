package de.ur.assistenz.emomusic;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private List<File> playList = new ArrayList<>();
    private SettingsManager settingsManager;

    public void start(Stage primaryStage) {
        settingsManager = SettingsManager.getInstance();

        primaryStage.setTitle("Hello World!");
        ListView appContent = new ListView<>();
        appContent.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                ListCell listCell = new ListCell(){
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(((File) item).getName());
                        }
                    }
                };
                listCell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        File item = (File) listCell.getItem();
                        Media song = new Media(item.toURI().toASCIIString());
                        MediaPlayer mediaPlayer = new MediaPlayer(song);
                        /* wenn bereits Song spielt, stoppe diesen, starte den neuen,
                        wenn auf den selben Song geklickt wurde - 1 klick - pause, doppelklick, neu spielen?
                        wenn pausiert, song fortsetzen (1 Klick)
                         */
                        mediaPlayer.play();
                    }
                });
                return listCell;
            }
        });
        ObservableList<File> observableList = FXCollections.observableList(playList);
        appContent.setItems(observableList);

        Button btnOpen = new Button("Open");
        btnOpen.getStyleClass().add("button");
        btnOpen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select music files");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3")
                );
                List<File> selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);
                observableList.addAll(selectedFiles);
            }
        });


        Button btnPlay = new Button(">");
        btnPlay.getStyleClass().add("button");
        btnPlay.setOnAction(new EventHandler<ActionEvent>() {
            boolean btnStatus = true;
            @Override

            public void handle(ActionEvent event) {
                if (btnStatus == true) {
                    btnPlay.setText("||");
                    btnStatus = false;
                }
                else {
                    btnPlay.setText(">");
                    btnStatus = true;
                }
            }
        });

        /*weitere Buttons: Stop, vor- bzw. zurück

         */


        BorderPane borderPane = new BorderPane();
        ToolBar toolbar = new ToolBar(btnOpen);
        HBox statusbar = new HBox(btnPlay);
        final Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(50);
        statusbar.getChildren().addAll(slider);
        borderPane.setTop(toolbar);
        borderPane.setCenter(appContent);
        borderPane.setBottom(statusbar);


        Scene scene = new Scene(borderPane, 500, 400);
        scene.getStylesheets().addAll("style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
