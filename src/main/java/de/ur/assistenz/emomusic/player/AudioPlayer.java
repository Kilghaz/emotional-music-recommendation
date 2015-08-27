package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Listener.AudioPlayerObserver;
import de.ur.assistenz.emomusic.sql.Song;
import javafx.beans.Observable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class AudioPlayer {

    private MediaPlayer mediaPlayer;
    private AudioPlayerObserver observer = new NullObserver();

    public void setObserver(AudioPlayerObserver observer) {
        this.observer = observer;
    }

    public void changeSong(Song song){
        stop();
        Media media = new Media(new File(song.getUrl()).toURI().toASCIIString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnPlaying(observer::onPlay);
        mediaPlayer.setOnPaused(observer::onPause);
        mediaPlayer.setOnStopped(observer::onStop);
        mediaPlayer.setOnEndOfMedia(this::stop);
        mediaPlayer.setOnError(observer::onPlayError);
        mediaPlayer.currentTimeProperty().addListener(observer::onProgress);
    }

    public void play(){
        if(mediaPlayer == null) {
            return;
        }
        mediaPlayer.play();
    }

    public void pause(){
        if(mediaPlayer == null) {
            return;
        }
        mediaPlayer.pause();
    }

    public void stop(){
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        observer.onSongFinished();
    }

    public Duration getCurrentTime() {
        return mediaPlayer.getCurrentTime();
    }

    public Duration getSongLength() {
        return mediaPlayer.getStopTime();
    }

    private class NullObserver implements AudioPlayerObserver {

        @Override
        public void onSongFinished() {}

        @Override
        public void onPlayError() {}

        @Override
        public void onPlay() {}

        @Override
        public void onPause() {
        }

        @Override
        public void onStop() {
        }

        @Override
        public void onProgress(Observable observable) {}

    }

}
