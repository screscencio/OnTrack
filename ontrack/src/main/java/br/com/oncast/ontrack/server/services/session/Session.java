package br.com.oncast.ontrack.server.services.session;

import java.io.Serializable;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

/**
 * Unique session information holder.<br/>
 * <b>Development Advice</b>: This class is designed to make all session information type-safe. Instead of using the {@link javax.servlet.http.HttpSession}
 * directly, favor creating fields in this class to maintain your session specific information.<br />
 * Remember to keep this class's non-transient memory footprint always extremely small since it is serialized, transported, stored and recreated at each http
 * request.
 */
public class Session implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(Session.class);

	private transient User authenticatedUser;

	private final String sessionId;

	private transient ThreadLocal<UUID> threadLocalClientId;

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
		if (authenticatedUser == null) {
			LOGGER.debug("The user logged out (sessionId = '" + this.sessionId + "')");
		}
		else {
			LOGGER.debug("The user '" + authenticatedUser.getEmail() + "' logged in (sessionId = '" + this.sessionId + "')");
		}
		this.authenticatedUser = authenticatedUser;
	}

	protected void setThreadLocalClientId(final UUID clientId) {
		loadThreadLocalClientId().set((clientId != null && clientId.isValid()) ? clientId : UUID.INVALID_UUID);
	}

	public UUID getThreadLocalClientId() {
		return loadThreadLocalClientId().get();
	}

	private ThreadLocal<UUID> loadThreadLocalClientId() {
		return threadLocalClientId == null ? threadLocalClientId = new ThreadLocal<UUID>() : threadLocalClientId;
	}

}
