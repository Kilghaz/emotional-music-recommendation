package de.ur.assistenz.emomusic.player.Observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventSender<EventType extends Event> {

    private HashMap<String, List<EventReceiver<EventType>>> events = new HashMap<>();

    public List<String> getObservableEvents() {
        ArrayList<String> keys = new ArrayList<>();
        keys.addAll(events.keySet());
        return keys;
    }

    public void register(String event) {
        this.events.put(event, new ArrayList<>());
    }

    public void unregister(String event) {
        this.events.remove(event);
    }

    public void notify(String event) {
        for(EventReceiver observer : events.get(event)) {
            observer.onEvent(this, new Event(event));
        }
    }

    public void notify(String event, HashMap<String, Object> data) {
        for(EventReceiver observer : events.get(event)) {
            observer.onEvent(this, new Event(event, data));
        }
    }

    public void notify(String event, String key, Object value) {
        for(EventReceiver observer : events.get(event)) {
            observer.onEvent(this, new Event(event, key, value));
        }
    }

    public void notify(String event, EventType evt) {
        evt.setName(event);
        for(EventReceiver observer : events.get(event)) {
            observer.onEvent(this, evt);
        }
    }

    public void on(String event, EventReceiver observable) {
        this.events.get(event).add(observable);
    }

    public void off(String event, EventReceiver observable) {
        this.events.get(event).remove(observable);
    }

}
