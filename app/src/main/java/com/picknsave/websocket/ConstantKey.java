package com.picknsave.websocket;

public class ConstantKey {
	public static final String TAG = "xlui";
	public static final String PLACEHOLDER = "placeholder";

	/**
	 * <code>im</code> in address is the endpoint configured in server.
	 * If you are using AVD provided by Android Studio, you should uncomment the upper address.
	 * If you are using Genymotion, nothing else to do.
	 * If you are using your own phone, just change the server address and port.
	 */
	//http://10.0.2.2:8080/
	// private static final String address = "ws://10.0.2.2:8080/im/websocket";
	public static final String SOCKET_SERVER_URL = "ws://10.0.2.2:8080/im/websocket";

	public static final String broadcast = "/broadcast";
	public static final String broadcastResponse = "/b";

	// replace {@code placeholder} with group id
	public static final String group = "/group/" + PLACEHOLDER;
	public static final String groupResponse = "/g/" + PLACEHOLDER;

	public static final String CHAT = "/chat";
	// replace {@code placeholder} with user id
	public static final String CHAT_RESPONSE = "/user/" + PLACEHOLDER + "/msg";
}
