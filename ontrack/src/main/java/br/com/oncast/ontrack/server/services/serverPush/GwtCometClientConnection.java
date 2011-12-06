package br.com.oncast.ontrack.server.services.serverPush;

class GwtCometClientConnection implements ServerPushConnection {

	private final String clientId;
	private final String sessionId;

	public GwtCometClientConnection(final String clientId, final String sessionId) {
		this.clientId = clientId;
		this.sessionId = sessionId;
	}

	@Override
	public String getClientId() {
		return clientId;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public int hashCode() {
		return clientId.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof GwtCometClientConnection)) return false;
		final GwtCometClientConnection other = (GwtCometClientConnection) obj;
		return clientId.equals(other.clientId);
	}

	@Override
	public String toString() {
		return "(cliendId='" + clientId + "', sessionId='" + sessionId + "')";
	}
}