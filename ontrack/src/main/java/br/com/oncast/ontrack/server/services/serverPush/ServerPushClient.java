package br.com.oncast.ontrack.server.services.serverPush;

// TODO Is this class really necessary?
public class ServerPushClient {

	private final String sessionId;

	public ServerPushClient(final String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

}
