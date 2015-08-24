package de.ur.assistenz.emomusic.player;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class PlayerWindow {

    private Stage stage;
    private PlayerController controller = new PlayerController();

    private PlaylistView playlistView = new PlaylistView();
    private PlayerControlsView playerControlsView = new PlayerControlsView();
    private AudioPlayer audioPlayer = new AudioPlayer();
    private ToolbarView toolbar = new ToolbarView(stage);

    private PlaylistModel playlistModel = new PlaylistModel();
    private MusicLibraryModel musicLibraryModel = MusicLibraryModel.getInstance();

    public PlayerWindow(Stage stage) {
        this.stage = stage;
        initGUI();
        initController();
    }

    private void initGUI() {
        stage.setTitle("E.M.O. Music Player");

        BorderPane layout = new BorderPane();
        layout.setTop(toolbar);
        layout.setCenter(playlistView);
        layout.setBottom(playerControlsView);

        Scene scene = new Scene(layout, 500, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void initController() {
        controller.setPlaylistView(playlistView);
        controller.setPlayerControlsView(playerControlsView);
        controller.setAudioPlayer(audioPlayer);
        controller.setPlaylistModel(playlistModel);
        controller.setMusicLibraryModel(musicLibraryModel);
        controller.setToolbar(toolbar);
        controller.init();
    }

}
