package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Observer.EventReceiver;
import de.ur.assistenz.emomusic.player.Observer.EventSender;
import de.ur.assistenz.emomusic.player.Observer.SongEvent;
import de.ur.assistenz.emomusic.sql.Song;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;

public class PlaylistView extends TableView<Song> {

    private static final String EVENT_SONG_SELECTED = "song_selected";

    private static final String CSS_CLASS_PLAYING = "playing";
    private static final String CSS_CLASS_SONG = "song";

    private int currentlyPlaying;

    private EventSender<SongEvent> eventSender = new EventSender<>();

    public PlaylistView() {
        initColumns();
        setItems(FXCollections.observableList(new ArrayList<>()));
        this.eventSender.register(EVENT_SONG_SELECTED);
        this.getStyleClass().add("playlist");
    }

    private void initColumns() {
        TableColumn<Song, String> titleColumn   = new TableColumn<>();
        TableColumn<Song, String> artistColumn  = new TableColumn<>();
        TableColumn<Song, String> yearColumn    = new TableColumn<>();
        TableColumn<Song, String> emotionColumn = new TableColumn<>();

        titleColumn.setCellValueFactory(cellElement -> new SimpleStringProperty(cellElement.getValue().getName()));
        titleColumn.setText("Title");
        titleColumn.setMinWidth(400);
        artistColumn.setCellValueFactory(cellElement -> new SimpleStringProperty(cellElement.getValue().getArtist()));
        artistColumn.setText("Artist");
        artistColumn.setMinWidth(200);
        yearColumn.setCellValueFactory(cellElement -> new SimpleStringProperty(cellElement.getValue().getYear()));
        yearColumn.setText("Year");
        yearColumn.setMinWidth(100);
        emotionColumn.setCellValueFactory(cellElement -> new SimpleStringProperty(cellElement.getValue().getEmotion()));
        emotionColumn.setText("Emotion");
        emotionColumn.setMinWidth(200);

        getColumns().add(titleColumn);
        getColumns().add(artistColumn);
        getColumns().add(yearColumn);
        getColumns().add(emotionColumn);
    }

    public void setCurrentlyPlaying(int index) {
        this.currentlyPlaying = index;
    }


    public void onSongSelected(EventReceiver<SongEvent> receiver) {
        this.eventSender.on(EVENT_SONG_SELECTED, receiver);
    }

}
