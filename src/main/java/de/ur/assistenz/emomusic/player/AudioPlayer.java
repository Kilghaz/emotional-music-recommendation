package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Listener.AudioPlayerObserver;
import de.ur.assistenz.emomusic.sql.Song;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioPlayer {

    private MediaPlayer mediaPlayer;
    private AudioPlayerObserver observer = new NullObserver();

    public void setObserver(AudioPlayerObserver observer) {
        this.observer = observer;
    }

    public void changeSong(Song song){
        stop();
        Media media = new Media(song.getUrl());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(this::stop);
        mediaPlayer.setOnError(observer::onPlayError);
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

    private class NullObserver implements AudioPlayerObserver {

        @Override
        public void onSongFinished() {}

        @Override
        public void onPlayError() {}

    }

}
