package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Observer.EventReceiver;
import de.ur.assistenz.emomusic.player.Observer.EventSender;
import de.ur.assistenz.emomusic.player.Observer.SongEvent;
import de.ur.assistenz.emomusic.sql.Song;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.ArrayList;

public class PlaylistView extends ListView<Song> {

    private static final String EVENT_SONG_SELECTED = "song_selected";

    private static final String CSS_CLASS_PLAYING = "playing";
    private static final String CSS_CLASS_SONG = "song";

    private int currentlyPlaying;

    private EventSender<SongEvent> eventSender = new EventSender<>();

    public PlaylistView() {
        setCellFactory(new SongCellFactory());
        setItems(FXCollections.observableList(new ArrayList<>()));
        this.eventSender.register(EVENT_SONG_SELECTED);
        this.getStyleClass().add("playlist");
    }

    public void setCurrentlyPlaying(int index) {
        this.currentlyPlaying = index;
        triggerUpdate(null, -1);
    }

    private void triggerUpdate(Song newValue, int index) {
        triggerUpdate(this, newValue, index);
    }

    private static <T> void triggerUpdate(ListView<T> listView, T newValue, int i) {
        EventType<? extends EditEvent<T>> type = ListView.editCommitEvent();
        Event event = new ListView.EditEvent<>(listView, type, newValue, i);
        listView.fireEvent(event);
    }

    public void onSongSelected(EventReceiver<SongEvent> receiver) {
        this.eventSender.on(EVENT_SONG_SELECTED, receiver);
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
                SongEvent songEvent = new SongEvent(listCell.getItem());
                songEvent.put("index", listCell.getIndex());
                eventSender.notify(EVENT_SONG_SELECTED, songEvent);
            });

            return listCell;
        }

    }

}
