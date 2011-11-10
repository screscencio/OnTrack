package br.com.oncast.ontrack.server.services.session;

import javax.servlet.http.HttpSession;

import br.com.oncast.ontrack.server.services.session.exceptions.SessionUnavailableException;

/**
 * Manager for {@link Session} localization and persistence.<br />
 * The manager relies on the one-thread-per-request strategy used by the Servlet specification to locate the {@link Session} whenever called.
 */
public class SessionManager {

	private final static ThreadLocal<Session> LOCAL_SESSION = new ThreadLocal<Session>();

	public void configureCurrentHttpSession(final HttpSession httpSession) {
		final Session session = getOrCreateSession(httpSession);
		LOCAL_SESSION.set(session);
	}

	private Session getOrCreateSession(final HttpSession httpSession) {
		final String attributeName = Session.class.getName();

		Session session = (Session) httpSession.getAttribute(attributeName);
		if (session == null) session = new Session();
		httpSession.setAttribute(attributeName, session);

		return session;
	}

	/**
	 * Returns the {@link Session} bound to this thread.
	 * @return the {@link Session} bound to this thread.
	 * @throws SessionUnavailableException in case there was no {@link Session} bound to the current thread.
	 */
	public Session getCurrentSession() {
		if (LOCAL_SESSION.get() == null) throw new SessionUnavailableException();
		return LOCAL_SESSION.get();
	}
}
