package de.ur.assistenz.emomusic.speech;

import de.ur.assistenz.emomusic.player.Observer.Event;
import de.ur.assistenz.emomusic.player.Observer.EventReceiver;
import de.ur.assistenz.emomusic.player.Observer.EventSender;
import de.ur.assistenz.emomusic.speech.processors.KeywordExtractionProcessor;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;

import java.io.IOException;
import java.util.List;

public class SpeechRecognizer {

    public static final SpeechEventProcessor DEFAULT_PROCESSOR = new KeywordExtractionProcessor();

    private EventSender<Event> eventSender = new EventSender<>();
    private LiveSpeechRecognizer recognizer = null;
    private SpeechRecognitionThread recognitionThread = null;
    private SpeechEventProcessor eventProcessor = null;

    public SpeechRecognizer(SpeechEventProcessor processor) {
        this.eventProcessor = processor;
        this.initEvents();
        this.initSpeechRecognition();
    }

    /**
     * Creates a speech recognizer using the default SpeechEventProcessor.
     */
    public SpeechRecognizer() {
        this(DEFAULT_PROCESSOR);
    }

    private void initEvents() {
        for(String eventKey : eventProcessor.getEventKeys()) {
            eventSender.register(eventKey);
        }
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

    /**
     * Stops the speech recognition
     */
    public void stopRecognition() {
        recognitionThread.setRunning(false);
    }

    /**
     * Starts the speech recognition
     */
    public void startRecognition() {
        if(!recognitionThread.isRunning()) {
            recognitionThread.start();
        }
    }

    /**
     * Register callback for speech recognition
     * @param receiver The Event receiver that implements the callback
     */
    public void onSpeechProcessed(EventReceiver receiver, SpeechEventProcessor eventProcessor) {
        for(String key : eventProcessor.getEventKeys()) {
            eventSender.on(key, receiver);
        }
    }

    private void processSpeechEvent(SpeechEvent speechEvent) {
        List<Event> events = eventProcessor.process(speechEvent);
        for(Event event : events) {
            eventSender.notify(event.getName(), event);
        }
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
                    processSpeechEvent(new SpeechEvent(recognizer.getResult()));
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
