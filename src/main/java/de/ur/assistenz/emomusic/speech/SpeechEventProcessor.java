package de.ur.assistenz.emomusic.speech;

import de.ur.assistenz.emomusic.player.Observer.Event;

import java.util.List;

public interface SpeechEventProcessor {

    /**
     * Should implement event processing and return an event string
     * or null if the speech event does not match or cannot be processed.
     * @param event The speech event
     * @return returns the event-identifier associated with the speech input
     */
    List<Event> process(SpeechEvent event);

    /**
     * @return Returns a list of all possible occuring events
     */
    List<String> getEventKeys();

}