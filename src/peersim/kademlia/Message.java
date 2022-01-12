package peersim.kademlia;

import java.math.BigInteger;

/**
 * Message class provide all functionalities to manage the various messages, principally LOOKUP messages (messages from
 * application level sender designated to another application level).
 */
// ______________________________________________________________________________________
public class Message extends SimpleEvent {

    /**
     * Message Type: EMPTY (used to construct empty message)
     */
    public static final int MSG_EMPTY = 0;
    /**
     * Message Type: FINDNODE (message regarding node find)
     */
    public static final int MSG_FINDNODE = 1;
    /**
     * Message Type: ROUTE (message during lookup)
     */
    public static final int MSG_ROUTE = 2;
    /**
     * Message Type: RESPONSE (response message to a findvalue or findnode)
     */
    public static final int MSG_RESPONSE = 3;
    /**
     * Message Type: RESPONSE (response message to a findvalue or findnode)
     */
    public static final int TIMEOUT = 4;

    /**
     * internal generator for unique message IDs
     */
    private static long ID_GENERATOR = 0;
    /**
     * This Object contains the body of the message, no matter what it contains
     */
    public Object body = null;

    /**
     * ID of the message. this is automatically generated univocally, and should not change
     */
    public long id;

    /**
     * ACK number of the message. This is in the response message.
     */
    public long ackId;

    /**
     * Id of the search operation
     */
    public long operationId;

    /**
     * Recipient address of the message
     */
    public KadNode dest;

    /**
     * Source address of the message: has to be filled at application level
     */
    public KadNode src;

    /**
     * Available to count the number of hops the message did.
     */
    protected int nrHops = 0;

    // ______________________________________________________________________________________________

    /**
     * Creates an empty message by using default values (message type = MSG_LOOKUP and <code>new String("")</code> value for the
     * body of the message)
     */
    public Message() {
        this(MSG_EMPTY, "");
    }

    /**
     * Create a message with specific type and empty body
     *
     * @param messageType int type of the message
     */
    public Message(int messageType) {
        this(messageType, "");
    }

    // ______________________________________________________________________________________________

    /**
     * Creates a message with specific type and body
     *
     * @param messageType int type of the message
     * @param body        Object body to assign (shallow copy)
     */
    public Message(int messageType, Object body) {
        super(messageType);
        this.id = (ID_GENERATOR++);
        this.body = body;
    }


    /**
     * Encapsulates the creation of a message.
     *
     * @param body Object
     * @return Message
     */
    public static final Message makeEmptyMessage(Object body, int type) {
        return new Message(type, body);
    }


    /**
     * Print the message info.
     *
     * @return The printed message.
     */
    public String toString() {
        String s = "[ID=" + id + "][DEST=" + dest + "]";
        return s + "[Type=" + messageTypetoString() + "] BODY=(...)";
    }

    /**
     * Copy the message.
     *
     * @return The copy message.
     */
    public Message copy() {
        Message dolly = new Message();
        dolly.type = this.type;
        dolly.src = this.src;
        dolly.dest = this.dest;
        dolly.operationId = this.operationId;
        dolly.body = this.body; // deep cloning?

        return dolly;
    }

    /**
     * Translates the type of message to a string.
     *
     * @return The corresponding string value.
     */
    public String messageTypetoString() {
        switch (type) {
            case MSG_EMPTY:
                return "MSG_EMPTY";
            case MSG_FINDNODE:
                return "MSG_FINDNODE";
            case MSG_ROUTE:
                return "MSG_ROUTE";
            case MSG_RESPONSE:
                return "MSG_RESPONSE";
            default:
                return "UNKNOWN:" + type;
        }
    }


    public void send(){

    }
}
