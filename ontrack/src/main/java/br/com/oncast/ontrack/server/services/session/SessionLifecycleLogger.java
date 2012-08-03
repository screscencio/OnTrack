package br.com.oncast.ontrack.server.services.session;

import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

public class SessionLifecycleLogger implements HttpSessionListener {

	private static final Logger LOGGER = Logger.getLogger(SessionLifecycleLogger.class);
	private static final int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;

	@Override
	public void sessionCreated(final HttpSessionEvent event) {
		LOGGER.debug("A new java session [" + event.getSession().getId() + "] was created.");
	}

	@Override
	public void sessionDestroyed(final HttpSessionEvent event) {
		final HttpSession httpSession = event.getSession();
		LOGGER.info("A java session was destroyed. Session [" + httpSession.getId() + "] had a lifetime of '" + getSessionDayLifetime(httpSession) + "' days.");
	}

	private int getSessionDayLifetime(final HttpSession httpSession) {
		final long sessionDestructionTime = new Date().getTime();
		final long sessionCreationTime = httpSession.getCreationTime();

		return (int) ((sessionDestructionTime - sessionCreationTime) / MILLIS_IN_DAY);
	}
}
