package de.ur.assistenz.emomusic.speech.processors;

import de.ur.assistenz.emomusic.player.Observer.Event;
import de.ur.assistenz.emomusic.speech.SpeechEvent;
import de.ur.assistenz.emomusic.speech.SpeechEventProcessor;
import edu.cmu.sphinx.linguist.dictionary.Word;
import edu.cmu.sphinx.result.WordResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class KeywordExtractionProcessor implements SpeechEventProcessor {

    private HashMap<String, List<String>> keywords = new HashMap<>();

    @Override
    public List<Event> process(SpeechEvent event) {
        List<Event> events = new ArrayList<>();
        for(WordResult wordResult : event.getResult().getWords()) {
            Word word = wordResult.getWord();
            for(String action : keywords.keySet()) {
                List<String> actionKeywords = keywords.get(action);
                for(String kw : actionKeywords) {
                    if(kw.toLowerCase().equals(word.getSpelling().toLowerCase())) {
                        events.add(new Event(action));
                    }
                }
            }
        }
        return events;
    }

    /**
     * Adds a keyword list with the corresponding action
     * @param action The action that matches the keyword
     * @param keywords The list of keywords
     */
    public void addKeyword(String action, String ... keywords) {
        List<String> keywordList = new ArrayList<>();
        Collections.addAll(keywordList, keywords);
        this.keywords.put(action, keywordList);
    }

    public List<String> getEventKeys() {
        List<String> eventKeys = new ArrayList<>();
        for(String key : this.keywords.keySet()) {
            eventKeys.add(key);
        }
        return eventKeys;
    }

}
