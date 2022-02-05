package peersim.kademlia;

import peersim.core.CommonState;

/**
 * This class defines a simple event. A simple event is characterized only by its type.
 */
public class SimpleEvent {

    /**
     * Timestamp when this event was created.
     */
    public long timestamp;
    /**
     * The identifier of the type of the event.
     */
    public int type;

    /**
     * Constructs the event.
     */
    public SimpleEvent() {
        this.timestamp = CommonState.getTime();
    }

    /**
     * Initializes the type of the event.
     *
     * @param type The identifier of the type of the event
     */
    public SimpleEvent(int type) {
        this();
        this.type = type;
    }

    /**
     * Gets the type of the event.
     *
     * @return The type of the current event.
     */
    public int getType() {
        return this.type;
    }

}