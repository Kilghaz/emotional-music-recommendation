package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Listener.ToolbarViewObserver;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class ToolbarView extends ToolBar {

    private Button btnOpen = new Button("Open");
    private Stage stage;
    private ToolbarViewObserver observer = new NullObserver();

    public ToolbarView(Stage stage){
        this.stage = stage;
        initGUI();
    }

    private void initGUI(){
        this.getItems().add(btnOpen);

        btnOpen.setOnAction(this::onOpenClicked);
    }

    private void onOpenClicked(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Music Library");
        File folder = directoryChooser.showDialog(this.stage);
        observer.onLibraryFolderSelected(folder);
    }

    public void setObserver(ToolbarViewObserver observer) {
        this.observer = observer;
    }

    private class NullObserver implements ToolbarViewObserver {

        @Override
        public void onLibraryFolderSelected(File file) {}

    }

}
