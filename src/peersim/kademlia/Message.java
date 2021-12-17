package peersim.kademlia;

import java.math.BigInteger;

/**
 * 
 * Message class provide all functionalities to manage the various messages, principally LOOKUP messages (messages from
 * application level sender designated to another application level).
 * 
 * Types Of messages:
 * 	(application messages)
 * 		- MSG_LOOKUP: indicates that the body Object contains information to application level of the recipient
 * 	(service internal protocol messages)
 * 		- MSG_JOINREQUEST: message containing a join request of a node, the message is passed between many pastry nodes according to
 * 						   the protocol
 * 		- MSG_JOINREPLY: according to protocol, the body transport information related to a join reply message
 * 		- MSG_LSPROBEREQUEST:according to protocol, the body transport information related to a probe request message <br>
 * 		- MSG_LSPROBEREPLY: not used in the current implementation<br>
 * 		- MSG_SERVICEPOLL: internal message used to provide cyclic cleaning service of dead nodes<br>
 */
// ______________________________________________________________________________________
public class Message extends SimpleEvent {

	/**
	 * internal generator for unique message IDs
	 */
	private static long ID_GENERATOR = 0;

	/**
	 * Message Type: EMPTY (used to construct empty message)
	 */
	public static final int MSG_EMPTY = 0;

	/**
	 * Message Type: STORE (Stores a (key, value) pair in one node)
	 */
	public static final int MSG_STORE = 1;

	/**
	 * Message Type: FINDNODE (message regarding node find)
	 */
	public static final int MSG_FINDNODE = 2;

	/**
	 * Message Type: ROUTE (message during lookup)
	 */
	public static final int MSG_ROUTE = 3;

	/**
	 * Message Type: RESPONSE (response message to a findvalue or findnode)
	 */
	public static final int MSG_RESPONSE = 4;

	/**
	 * Message Type: PING (used to verify that a node is still alive)
	 */
	public static final int MSG_PING = 5;

	/**
	 * Message Type: FINDVALUE (used to verify that a node is still alive)
	 */
	public static final int MSG_FINDVALUE = 6;


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
	public BigInteger dest;

	/**
	 * Source address of the message: has to be filled at application level
	 */
	public BigInteger src;

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
	 * @param messageType
	 *            int type of the message
	 */
	public Message(int messageType) {
		this(messageType, "");
	}

	// ______________________________________________________________________________________________
	/**
	 * Creates a message with specific type and body
	 * 
	 * @param messageType
	 *            int type of the message
	 * @param body
	 *            Object body to assign (shallow copy)
	 */
	public Message(int messageType, Object body) {
		super(messageType);
		this.id = (ID_GENERATOR++);
		this.body = body;
	}

	/*
	/**
	 * Encapsulates the creation of a find value request
	 * 
	 * @param body
	 *            Object
	 * @return Message
	/*public static final Message makeFindValue(Object body) {
		return new Message(MSG_FINDVALUE, body);
	}*/


	/**
	 * Encapsulates the creation of a message.
	 * 
	 * @param body
	 *            Object
	 * @return Message
	 */
	public static final Message makeEmptyMessage(Object body, int type) {
		return new Message(type, body);
	}


	/**
	 * Print the message info.
	 * @return
	 * 			The printed message.
	 */
	public String toString() {
		String s = "[ID=" + id + "][DEST=" + dest + "]";
		return s + "[Type=" + messageTypetoString() + "] BODY=(...)";
	}

	/**
	 * Copy the message.
	 * @return
	 * 			The copy message.
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
	 * @return
	 * 			The corresponding string value.
	 */
	public String messageTypetoString() {
		switch (type) {
			case MSG_EMPTY:
				return "MSG_EMPTY";
			case MSG_STORE:
				return "MSG_STORE";
			case MSG_FINDNODE:
				return "MSG_FINDNODE";
			case MSG_ROUTE:
//				return "MSG_FINDVALUE";
				return "MSG_ROUTE";
			case MSG_RESPONSE:
				return "MSG_RESPONSE";
			case MSG_PING:
				return "MSG_PING";
			case MSG_FINDVALUE:
				return "MSG_FINDVALUE";
			default:
				return "UNKNOWN:" + type;
		}
	}
}
