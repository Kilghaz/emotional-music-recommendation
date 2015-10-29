package de.ur.assistenz.emomusic.speech;

import de.ur.assistenz.emomusic.player.Observer.Event;
import edu.cmu.sphinx.api.SpeechResult;

public class SpeechEvent extends Event {

    private SpeechResult result;

    public SpeechEvent(SpeechResult result) {
        this.result = result;
    }

    public SpeechResult getResult() {
        return result;
    }

}
