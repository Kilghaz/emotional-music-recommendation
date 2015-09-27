package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Observer.Event;
import de.ur.assistenz.emomusic.player.Observer.EventSender;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PlayerWindow {

    private static final String TITLE = "E.M.O. Music Player";
    private static final String STYLESHEET = "style.css";
    private static final String ICON = "file:resources/images/icon.png";

    private Stage stage;

    private PlaylistView playlistView = new PlaylistView();
    private PlayerControlsView playerControlsView = new PlayerControlsView();
    private PlaylistSelectionView playlistSelectionView = new PlaylistSelectionView();
    private AudioPlayer audioPlayer = new AudioPlayer();
    private ToolbarView toolbar = new ToolbarView(stage);

    private PlaylistSelectionModel playlistSelectionModel = new PlaylistSelectionModel();
    private PlaylistModel playlistModel = new PlaylistModel();
    private MusicLibraryModel musicLibraryModel = MusicLibraryModel.getInstance();

    public PlayerWindow(Stage stage) {
        try {
            this.stage = stage;
            initGUI();
            initController();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initGUI() {
        stage.setTitle(TITLE);
        stage.getIcons().add(new Image(ICON));

        BorderPane layout = new BorderPane();
        layout.getStyleClass().add("player-window");
        layout.setTop(toolbar);
        layout.setCenter(playlistView);
        layout.setBottom(playerControlsView);
        layout.setRight(playlistSelectionView);


        Scene scene = new Scene(layout, 902, 800);
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
        playerControlsView.onBackwardClicked((sender, event) -> {
            playlistModel.previous();
        });
        playerControlsView.onForwardClicked((sender, event) -> {
            playlistModel.next();
        });

        playlistModel.onSongChanged((sender, event) -> {
            playlistView.setCurrentlyPlaying(playlistModel.getCurrentSongIndex());
            audioPlayer.changeSong(playlistModel.getCurrentSong());
            audioPlayer.play();
        });

        playlistModel.onListChanged((sender, event) -> {
            playlistView.getItems().clear();
            playlistView.getItems().addAll(playlistModel.getSongs());
        });

        playlistView.onSongSelected((sender, event) -> playlistModel.setCurrentSong(event.getSong()));

        toolbar.onLibraryFolderSelected((sender, event) -> {
            musicLibraryModel.deleteAll();
            musicLibraryModel.setLibraryFolder(event.getFile());
            musicLibraryModel.scan();
        });

        playlistSelectionModel.onPlaylistChanged((sender, event) -> {
            SongFilter filter = playlistSelectionModel.getCurrentPlaylistFilter();
            playlistModel.setSongs(musicLibraryModel.fetchSongs(filter));
            playlistSelectionView.setActiveButton(playlistSelectionModel.getPlaylist());
        });

        playlistSelectionView.onLibraryClicked((sender, event) -> playlistSelectionModel.setPlaylist(PlaylistSelectionModel.PLAYLIST_LIBRARY));
        playlistSelectionView.onAngryClicked((sender, event) -> playlistSelectionModel.setPlaylist(PlaylistSelectionModel.PLAYLIST_ANGRY));
        playlistSelectionView.onHappyPlayClicked((sender, event) -> playlistSelectionModel.setPlaylist(PlaylistSelectionModel.PLAYLIST_HAPPY));
        playlistSelectionView.onCalmClicked((sender, event) -> playlistSelectionModel.setPlaylist(PlaylistSelectionModel.PLAYLIST_CALM));
        playlistSelectionView.onSadClicked((sender, event) -> playlistSelectionModel.setPlaylist(PlaylistSelectionModel.PLAYLIST_SAD));

        playlistModel.setSongs(musicLibraryModel.fetchSongs());
        playlistSelectionView.setActiveButton(playlistSelectionModel.getPlaylist());
    }

    private void onMusicLibraryUpdated(EventSender eventSender, Event musicLibraryEvent) {
        SongFilter filter = playlistSelectionModel.getCurrentPlaylistFilter();
        playlistModel.setSongs(musicLibraryModel.fetchSongs(filter));
    }

}
