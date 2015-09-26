package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Observer.EventReceiver;
import de.ur.assistenz.emomusic.player.Observer.EventSender;
import de.ur.assistenz.emomusic.player.Observer.FileEvent;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class ToolbarView extends ToolBar {

    private static final String EVENT_LIBRARY_FOLDER_SELECTED = "lib_selected";

    private Button btnOpen = new Button("Open");
    private Stage stage;

    private EventSender<FileEvent> eventSender = new EventSender<>();

    public ToolbarView(Stage stage){
        this.stage = stage;
        this.getStyleClass().add("toolbar");
        this.eventSender.register(EVENT_LIBRARY_FOLDER_SELECTED);
        initGUI();
    }

    private void initGUI(){
        this.getItems().add(btnOpen);
        btnOpen.setOnAction(this::onOpenClicked);

        this.btnOpen.getStyleClass().addAll("button", "btn-open");
    }

    public void onLibraryFolderSelected(EventReceiver<FileEvent> receiver) {
        this.eventSender.on(EVENT_LIBRARY_FOLDER_SELECTED, receiver);
    }

    private void onOpenClicked(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Music Library");
        File folder = directoryChooser.showDialog(this.stage);
        if(folder != null) {
            this.eventSender.notify(EVENT_LIBRARY_FOLDER_SELECTED, new FileEvent(folder));
        }
    }

}
