package de.ur.assistenz.emomusic.speech;

import de.ur.assistenz.emomusic.player.Observer.Event;
import de.ur.assistenz.emomusic.player.Observer.EventReceiver;
import de.ur.assistenz.emomusic.player.Observer.EventSender;
import de.ur.assistenz.emomusic.speech.processors.KeywordExtractionProcessor;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import javafx.application.Platform;

import java.io.IOException;
import java.util.List;

public class SpeechRecognizer {

    public static final SpeechEventProcessor DEFAULT_PROCESSOR = new KeywordExtractionProcessor();

    private EventSender<Event> eventSender = new EventSender<>();
    private LiveSpeechRecognizer recognizer = null;
    private SpeechRecognitionThread recognitionThread = null;
    private SpeechEventProcessor eventProcessor = null;

    private SpeechRecognizer(SpeechEventProcessor processor) {
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
        eventProcessor.getEventKeys().forEach(eventSender::register);
    }

    /**
     * Sets the events processor, that is used to post-process speech input
     *
     * @param eventProcessor The event Processor
     */
    public void setEventProcessor(SpeechEventProcessor eventProcessor) {
        this.eventProcessor.getEventKeys().forEach(eventSender::unregister);
        this.eventProcessor = eventProcessor;
        this.initEvents();
    }

    /**
     * @return returns the speech event processor
     */
    public SpeechEventProcessor getEventProcessor() {
        return eventProcessor;
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

        recognitionThread = new SpeechRecognitionThread();
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
    public void onSpeechProcessed(EventReceiver receiver) {
        for(String key : eventProcessor.getEventKeys()) {
            eventSender.on(key, receiver);
        }
    }

    private void processSpeechEvent(SpeechEvent speechEvent) {
        List<Event> events = eventProcessor.process(speechEvent);
        for(Event event : events) {
            Platform.runLater(() -> eventSender.notify(event.getName(), event));
        }
    }

    private class SpeechRecognitionThread extends Thread implements Runnable {

        private boolean running;

        @Override
        public void run() {
            while (running) {
                recognizer.startRecognition(true);
                processSpeechEvent(new SpeechEvent(recognizer.getResult()));
                recognizer.stopRecognition();
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
