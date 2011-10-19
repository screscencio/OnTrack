package br.com.oncast.ontrack.server.services.session;

import javax.servlet.http.HttpSession;

import br.com.oncast.ontrack.server.services.session.exceptions.SessionUnavailableException;

/**
 * Manager for {@link Session} localization and persistence.<br />
 * The manager relies on the one-thread-per-request strategy used by the Servlet specification to locate the {@link Session} whenever called.
 */
public class SessionManager {
	private final static ThreadLocal<Session> LOCAL_SESSION = new ThreadLocal<Session>();
	private final static ThreadLocal<HttpSession> LOCAL_HTTP_SESSION = new ThreadLocal<HttpSession>();

	public static void setCurrentHttpSession(final HttpSession httpSession) {
		LOCAL_HTTP_SESSION.set(httpSession);

		final Session session = (Session) httpSession.getAttribute(Session.class.getName());
		LOCAL_SESSION.set(session == null ? new Session() : session);
	}

	/**
	 * Returns the {@link Session} bound to this thread.
	 * @return
	 * @throws SessionUnavailableException in case there was no {@link Session} bound to the current thread.
	 */
	public static Session getCurrentSession() {
		if (LOCAL_SESSION.get() == null) throw new SessionUnavailableException();
		return LOCAL_SESSION.get();
	}

	/**
	 * Persist the user {@link Session session} into the {@link HttpSession}.<br />
	 * This method is needed since the GAE session persistence strategy is not triggered automatically. This method is meant to be called whenever a request
	 * finishes processing.
	 * @param httpSession
	 */
	public static void persistSession(final HttpSession httpSession) {
		httpSession.setAttribute(Session.class.getName(), getCurrentSession());
	}
}
