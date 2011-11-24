package br.com.oncast.ontrack.server.services.serverPush;

class GwtCometClientConnection implements ServerPushConnection {

	private final String clientId;

	public GwtCometClientConnection(final String clientId) {
		this.clientId = clientId;
	}

	@Override
	public String getClientId() {
		return clientId;
	}

	@Override
	public int hashCode() {
		return clientId.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final GwtCometClientConnection other = (GwtCometClientConnection) obj;
		if (clientId == null) {
			if (other.clientId != null) return false;
		}
		else if (!clientId.equals(other.clientId)) return false;
		return true;
	}

	@Override
	public String toString() {
		return clientId;
	}
}