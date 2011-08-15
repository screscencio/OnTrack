package br.com.oncast.ontrack.server.services.httpSessionProvider;

import javax.servlet.http.HttpSession;

public class HttpSessionProvider {

	private final ThreadLocal<HttpSession> httpSessionContainer = new ThreadLocal<HttpSession>();

	public HttpSession getCurrentSession() {
		final HttpSession httpSession = httpSessionContainer.get();
		if (httpSession == null) throw new RuntimeException("There is no http session set.");
		return httpSession;
	}

	void setCurrentSession(final HttpSession currentSession) {
		httpSessionContainer.set(currentSession);
	}
}