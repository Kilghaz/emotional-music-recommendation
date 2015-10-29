package de.ur.assistenz.emomusic.speech;

import de.ur.assistenz.emomusic.player.Observer.EventReceiver;
import de.ur.assistenz.emomusic.player.Observer.EventSender;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;

import java.io.IOException;

public class SpeechRecognizer {

    private static final String EVENT_SPEECH_RECOGNIZED = "speech_recognized";

    private static SpeechRecognizer instance = null;

    private EventSender<SpeechEvent> eventSender = new EventSender<>();
    private LiveSpeechRecognizer recognizer = null;
    private SpeechRecognitionThread recognitionThread = null;

    private SpeechRecognizer() {
        instance = this;
        this.initEvents();
        this.initSpeechRecognition();
    }

    /**
     * @return returns the SpeechRecognizer instance.
     */
    public static synchronized SpeechRecognizer getInstance() {
        return instance == null ? new SpeechRecognizer() : instance;
    }

    private void initEvents() {
        eventSender.register(EVENT_SPEECH_RECOGNIZED);
    }

    private void initSpeechRecognition() {
        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

        try {
            recognizer = new LiveSpeechRecognizer(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }

        recognitionThread = new SpeechRecognitionThread(10000);
        recognitionThread.start();
    }

    public void stopRecognition() {
        recognitionThread.setRunning(false);
    }

    public void startRecognition() {
        if(!recognitionThread.isRunning()) {
            recognitionThread.start();
        }
    }

    /**
     * Register callback for speech recognition
     * @param receiver The Event receiver that implements the callback
     */
    public void onSpeechRecognized(EventReceiver receiver) {
        eventSender.on(EVENT_SPEECH_RECOGNIZED, receiver);
    }

    private class SpeechRecognitionThread extends Thread implements Runnable {

        private boolean running;
        private long timeBetweenCallbacks;

        public SpeechRecognitionThread(long timeBetweenCallbacks) {
            this.timeBetweenCallbacks = timeBetweenCallbacks;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    recognizer.startRecognition(true);
                    // TODO: disable wait if sphinx already blocks
                    wait(this.timeBetweenCallbacks);
                    eventSender.notify(EVENT_SPEECH_RECOGNIZED, new SpeechEvent(recognizer.getResult()));
                    recognizer.stopRecognition();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        public boolean isRunning() {
            return running;
        }

        @Override
        public synchronized void start() {
            this.running = true;
            super.start();
        }

    }

}
