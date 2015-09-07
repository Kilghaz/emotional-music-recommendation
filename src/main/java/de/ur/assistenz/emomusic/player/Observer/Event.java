package de.ur.assistenz.emomusic.player.Observer;

import java.util.HashMap;

public class Event {

    private String name = null;
    private HashMap<String, Object> data = new HashMap<>();

    public Event(){}

    public Event(String name) {
        this.name = name;
    }

    public Event(String name, HashMap<String, Object> data) {
        this(name);
        this.data = data;
    }

    public Event(String name, String key, Object value) {
        this(name);
        put(key, value);
    }

    public void put(String key, Object value) {
        this.data.put(key, value);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public String getName() {
        return this.name;
    }

    protected void setName(String name) {
        this.name = name;
    }

}
