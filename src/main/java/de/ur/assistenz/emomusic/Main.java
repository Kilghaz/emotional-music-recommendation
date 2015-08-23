package de.ur.assistenz.emomusic;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private List<File> playList = new ArrayList<>();
    private SettingsManager settingsManager;
    private ObservableList<File> observableList;

    public void start(Stage primaryStage) {
        settingsManager = SettingsManager.getInstance();
        primaryStage.setTitle("E.M.O. Music Player");
        ListView appContent = new ListView<>();
        appContent.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                ListCell listCell = new ListCell() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(((File) item).getName());
                        }
                    }
                };
                listCell.setOnMouseClicked(event -> {
                    File item = (File) listCell.getItem();
                    Media song = new Media(item.toURI().toASCIIString());
                    MediaPlayer mediaPlayer = new MediaPlayer(song);
                    mediaPlayer.play();
                });
                return listCell;
            }
        });
        observableList = FXCollections.observableList(playList);
        appContent.setItems(observableList);

        Button btnOpen = new Button("Open");
        btnOpen.getStyleClass().add("button");
        btnOpen.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Music Library");
            File folder = directoryChooser.showDialog(primaryStage);
            settingsManager.saveText("library", folder.getAbsolutePath());
            loadLibrary();
        });


        Button btnPlay = new Button(">");
        btnPlay.getStyleClass().add("button");
        btnPlay.setOnAction(new EventHandler<ActionEvent>() {
            boolean btnStatus = true;

            @Override
            public void handle(ActionEvent event) {
                if (btnStatus) {
                    btnPlay.setText("||");
                    btnStatus = false;
                } else {
                    btnPlay.setText(">");
                    btnStatus = true;
                }
            }

        });

        // weitere Buttons: Stop, vor- bzw. zurück
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

        try {
            loadLibrary();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLibrary() {
        String libraryFolderPath = settingsManager.loadText("library");
        if(libraryFolderPath == null) return;
        File libraryFolder = new File(libraryFolderPath);
        if(!libraryFolder.exists()) return;
        File[] files = libraryFolder.listFiles((dir, name) -> name.endsWith(".mp3") || name.endsWith(".wav"));
        observableList.removeAll();
        observableList.addAll(files);
    }

}
