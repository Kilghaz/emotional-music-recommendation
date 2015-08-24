package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Listener.*;
import de.ur.assistenz.emomusic.sql.Song;
import javafx.event.ActionEvent;
import javafx.scene.input.DragEvent;

import java.io.File;
import java.util.List;

public class PlayerController implements PlaylistModelObserver, PlaylistViewObserver, PlayerControlsObserver, AudioPlayerObserver, ToolbarViewObserver, MusicLibraryModelObserver {

    private PlaylistView playlistView;
    private PlaylistModel playlistModel;
    private PlayerControlsView playerControlsView;
    private AudioPlayer audioPlayer;
    private ToolbarView toolbar;
    private MusicLibraryModel musicLibraryModel;

    public void init() {
        List<Song> songs = musicLibraryModel.fetchSongs();
        playlistView.getItems().addAll(songs);
    }

    public void setMusicLibraryModel(MusicLibraryModel musicLibraryModel) {
        this.musicLibraryModel = musicLibraryModel;
        this.musicLibraryModel.setObserver(this);
    }

    public void setAudioPlayer(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.audioPlayer.setObserver(this);
    }

    public void setPlaylistView(PlaylistView playlistView) {
        this.playlistView = playlistView;
        this.playlistView.setObserver(this);
    }

    public void setPlaylistModel(PlaylistModel playlistModel) {
        this.playlistModel = playlistModel;
        this.playlistModel.setObserver(this);
    }

    public void setPlayerControlsView(PlayerControlsView playerControlsView) {
        this.playerControlsView = playerControlsView;
        this.playerControlsView.setObserver(this);
    }

    public void setToolbar(ToolbarView toolbar) {
        this.toolbar = toolbar;
        this.toolbar.setObserver(this);
    }

    @Override
    public void onPlaylistChanged(List<Song> playlist) {
        playlistView.getItems().removeAll();
        playlistView.getItems().addAll(playlist);
    }

    @Override
    public void onSongChanged(int index, Song song) {
        playlistView.setCurrentlyPlaying(index);
        audioPlayer.changeSong(song);
    }

    @Override
    public void onSongSelected(int index, Song song) {
        playlistModel.selectSong(index);
    }

    @Override
    public void onPlayButtonClicked(ActionEvent event) {
        audioPlayer.play();
    }

    @Override
    public void onProgressBarDrag(DragEvent event) {
        // TODO: set audio player progress
    }

    @Override
    public void onSongFinished() {
        playlistModel.next();
    }

    @Override
    public void onPlayError() {
    }

    @Override
    public void onLibraryFolderSelected(File file) {
        musicLibraryModel.setLibraryFolder(file);
        musicLibraryModel.scan();
    }

    @Override
    public void onScanFinished() {
        // TODO: handle scan finished
        System.out.println("scan started");
    }

    @Override
    public void onScanStarted() {
        // TODO: handle scan started
        System.out.println("scan finished");
    }

    @Override
    public void onSongAdded(Song song) {
        playlistView.getItems().addAll(song);
        playlistView.sort();
    }

    @Override
    public void onSongDeleted(Song song) {
        playlistView.getItems().remove(song);
        playlistView.sort();
    }

    @Override
    public void onSongUpdated(Song song) {
        playlistView.getItems().removeIf(songToRemove -> songToRemove.getName().equals(song.getName()));
        playlistView.getItems().add(song);
        playlistView.sort();
    }

}
