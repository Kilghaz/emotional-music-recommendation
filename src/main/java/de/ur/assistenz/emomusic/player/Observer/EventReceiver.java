package de.ur.assistenz.emomusic.player.Observer;

public interface EventReceiver<EventType extends Event> {

    void onEvent(EventSender sender, EventType event);

}
