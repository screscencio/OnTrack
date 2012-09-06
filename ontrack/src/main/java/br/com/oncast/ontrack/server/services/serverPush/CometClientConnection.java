package br.com.oncast.ontrack.server.services.serverPush;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class CometClientConnection implements ServerPushConnection {

	private final String clientId;
	private final String sessionId;

	public CometClientConnection(final String clientId, final String sessionId) {
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
		return new HashCodeBuilder().append(clientId).append(sessionId).toHashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return "(cliendId='" + clientId + "', sessionId='" + sessionId + "')";
	}
}