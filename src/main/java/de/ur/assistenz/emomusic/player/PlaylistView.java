package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Listener.PlaylistViewObserver;
import de.ur.assistenz.emomusic.sql.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.ArrayList;

public class PlaylistView extends ListView<Song> {

    private static final String CSS_CLASS_PLAYING = "playing";
    private static final String CSS_CLASS_SONG = "song";

    private ObservableList<Song> songs;
    private int currentlyPlaying;

    private PlaylistViewObserver observer = new NullObserver();

    public PlaylistView() {
        setCellFactory(new SongCellFactory());
        songs = FXCollections.observableList(new ArrayList<>());
        setItems(songs);
    }

    public void setObserver(PlaylistViewObserver observer) {
        this.observer = observer;
    }

    public void setCurrentlyPlaying(int index) {
        this.currentlyPlaying = index;
        this.songs.notifyAll(); // send update to render list
    }

    private class SongCellFactory implements Callback<ListView<Song>, ListCell<Song>>{

        @Override
        public ListCell<Song> call(ListView<Song> listView) {
            ListCell<Song> listCell = new ListCell<Song>() {

                @Override
                protected void updateItem(Song song, boolean empty) {
                    super.updateItem(song, empty);
                    if (song == null) return;
                    this.getStyleClass().removeAll();
                    this.getStyleClass().addAll(CSS_CLASS_SONG);
                    if (getIndex() == currentlyPlaying) {
                        this.getStyleClass().add(CSS_CLASS_PLAYING);
                    }
                    setText(song.getName());
                }

            };

            listCell.setOnMouseClicked(event -> {
                observer.onSongSelected(listCell.getIndex(), listCell.getItem());
            });

            return listCell;
        }

    }

    public void sort() {
        getItems().sort((a, b) -> a.getName().compareTo(b.getName()));
    }

    private class NullObserver implements PlaylistViewObserver {

        @Override
        public void onSongSelected(int index, de.ur.assistenz.emomusic.sql.Song song) {}

    }

}
