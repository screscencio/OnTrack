package br.com.oncast.ontrack.server.services.serverPush;

class GwtCometClientConnection implements ServerPushConnection {

	private final String sessionId;

	public GwtCometClientConnection(final String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	@Override
	public int hashCode() {
		return sessionId.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final GwtCometClientConnection other = (GwtCometClientConnection) obj;
		if (sessionId == null) {
			if (other.sessionId != null) return false;
		}
		else if (!sessionId.equals(other.sessionId)) return false;
		return true;
	}

	@Override
	public String toString() {
		return sessionId;
	}
}