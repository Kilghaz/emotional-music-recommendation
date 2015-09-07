package de.ur.assistenz.emomusic.player;

import de.ur.assistenz.emomusic.player.Observer.Event;
import de.ur.assistenz.emomusic.player.Observer.EventReceiver;
import de.ur.assistenz.emomusic.player.Observer.EventSender;
import de.ur.assistenz.emomusic.sql.Song;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class AudioPlayer {

    private static final String EVENT_PLAY = "play";
    private static final String EVENT_PAUSE = "pause";
    private static final String EVENT_STOP = "stop";
    private static final String EVENT_END_OF_MEDIA = "end_of_media";
    private static final String EVENT_ERROR = "error";
    private static final String EVENT_PROGRESS = "progress";

    private MediaPlayer mediaPlayer;
    private EventSender<Event> eventSender = new EventSender<>();

    public AudioPlayer() {
        eventSender.register(EVENT_PLAY);
        eventSender.register(EVENT_PAUSE);
        eventSender.register(EVENT_STOP);
        eventSender.register(EVENT_END_OF_MEDIA);
        eventSender.register(EVENT_ERROR);
        eventSender.register(EVENT_PROGRESS);
    }

    public void onPlay(EventReceiver eventReceiver) {
        eventSender.on(EVENT_PLAY, eventReceiver);
    }

    public void onPause(EventReceiver eventReceiver) {
        eventSender.on(EVENT_PAUSE, eventReceiver);
    }

    public void onStop(EventReceiver eventReceiver) {
        eventSender.on(EVENT_STOP, eventReceiver);
    }

    public void onProgress(EventReceiver eventReceiver) {
        eventSender.on(EVENT_PROGRESS, eventReceiver);
    }

    public void onError(EventReceiver eventReceiver) {
        eventSender.on(EVENT_ERROR, eventReceiver);
    }

    public void onEndOfMedia(EventReceiver eventReceiver) {
        eventSender.on(EVENT_END_OF_MEDIA, eventReceiver);
    }

    public void changeSong(Song song){
        stop();
        Media media = new Media(new File(song.getUrl()).toURI().toASCIIString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnPlaying(() -> eventSender.notify(EVENT_PLAY));
        mediaPlayer.setOnPaused(() -> eventSender.notify(EVENT_PAUSE));
        mediaPlayer.setOnStopped(() -> eventSender.notify(EVENT_STOP));
        mediaPlayer.setOnError(() -> eventSender.notify(EVENT_ERROR));
        mediaPlayer.currentTimeProperty().addListener(observable -> eventSender.notify(EVENT_PROGRESS));
        mediaPlayer.setOnEndOfMedia(() -> {
            this.stop();
            eventSender.notify(EVENT_END_OF_MEDIA);
        });
    }

    public void play(){
        if(this.mediaPlayer == null) return;
        mediaPlayer.play();
    }

    public void pause(){
        if(this.mediaPlayer == null) return;
        mediaPlayer.pause();
    }

    public void stop(){
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        eventSender.notify(EVENT_STOP);
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);
    }

    public Duration getCurrentTime() {
        if(this.mediaPlayer == null) return new Duration(0);
        return mediaPlayer.getCurrentTime();
    }

    public Duration getSongLength() {
        if(this.mediaPlayer == null) return new Duration(0);
        return mediaPlayer.getStopTime();
    }

    public void seek(double millis) {
        if(this.mediaPlayer == null) return;
        this.mediaPlayer.seek(new Duration(millis));
    }

}
