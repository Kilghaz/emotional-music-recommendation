package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Observer.Event;
import de.ur.assistenz.emomusic.player.Observer.EventSender;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PlayerWindow {

    private static final String STYLESHEET = "style.css";

    private Stage stage;

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
        layout.getStyleClass().add("player-window");
        layout.setTop(toolbar);
        layout.setCenter(playlistView);
        layout.setBottom(playerControlsView);

        Scene scene = new Scene(layout, 500, 400);
        scene.getStylesheets().addAll(STYLESHEET);

        stage.setScene(scene);
        stage.show();
    }

    private void initController() {
        audioPlayer.onPlay((sender, event) -> playerControlsView.onPlay());
        audioPlayer.onPause((sender, event) -> playerControlsView.onPause());
        audioPlayer.onStop((sender, event) -> playerControlsView.onPause());

        audioPlayer.onProgress((sender, event) -> {
            Duration currentTime = audioPlayer.getCurrentTime();
            Duration maxTime = audioPlayer.getSongLength();
            playerControlsView.updateProgressSliderPosition(currentTime, maxTime);
        });

        audioPlayer.onEndOfMedia((sender, event) -> playlistModel.next());

        musicLibraryModel.onSongAdded(this::onMusicLibraryUpdated);
        musicLibraryModel.onSongUpdated(this::onMusicLibraryUpdated);
        musicLibraryModel.onSongDeleted(this::onMusicLibraryUpdated);

        playerControlsView.onSeekerMoved((sender, event) -> audioPlayer.seek(playerControlsView.getSeekerValue()));
        playerControlsView.onPlayClicked((sender, event) -> {
            if (audioPlayer.isPlaying()) audioPlayer.pause();
            else audioPlayer.play();
        });

        playlistModel.onSongChanged((sender, event) -> {
            playlistView.setCurrentlyPlaying(playlistModel.getCurrentSongIndex());
            audioPlayer.changeSong(playlistModel.getCurrentSong());
            audioPlayer.play();
        });

        playlistModel.onListChanged((sender, event) -> {
            playlistView.getItems().clear();
            playlistView.getItems().addAll(playlistModel.getSongs());
            playlistView.sort();
        });

        playlistView.onSongSelected((sender, event) -> playlistModel.setCurrentSong(event.getSong()));

        toolbar.onLibraryFolderSelected((sender, event) -> {
            musicLibraryModel.deleteAll();
            musicLibraryModel.setLibraryFolder(event.getFile());
            musicLibraryModel.scan();
        });

        playlistModel.setSongs(musicLibraryModel.fetchSongs());
    }

    private void onMusicLibraryUpdated(EventSender eventSender, Event musicLibraryEvent) {
        // TODO: create actual playlists
        playlistModel.setSongs(musicLibraryModel.fetchSongs());
    }

}
