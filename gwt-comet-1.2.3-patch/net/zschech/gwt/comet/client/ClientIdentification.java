package net.zschech.gwt.comet.client;

public class ClientIdentification {

	private static String clientId;

	public static void setClientId(String clientId) {
		ClientIdentification.clientId = clientId;
	}

	public static String getClientId() {
		if (clientId == null) throw new RuntimeException("Cannot retrieve client id because it was not set.");
		return clientId;
	}
	
}
