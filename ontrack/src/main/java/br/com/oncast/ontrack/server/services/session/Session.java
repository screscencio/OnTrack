package br.com.oncast.ontrack.server.services.session;

import java.io.Serializable;

import br.com.oncast.ontrack.shared.model.user.User;

/**
 * Unique session information holder.<br/>
 * <b>Development Advice</b>: This class is designed to make all session information type-safe. Instead of using the {@link javax.servlet.http.HttpSession}
 * directly, favor creating fields in this class to maintain your session specific information.<br />
 * Remember to keep this class's non-transient memory footprint always extremely small since it is serialized, transported, stored and recreated at each http
 * request.
 */
public class Session implements Serializable {
	private static final long serialVersionUID = 1L;

	private transient User authenticatedUser;

	private final String sessionId;

	public Session(final String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public User getAuthenticatedUser() {
		return authenticatedUser;
	}

	public void setAuthenticatedUser(final User authenticatedUser) {
		this.authenticatedUser = authenticatedUser;
	}
}
