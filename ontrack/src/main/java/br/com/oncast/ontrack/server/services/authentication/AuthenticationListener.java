package br.com.oncast.ontrack.server.services.authentication;

import br.com.oncast.ontrack.shared.model.user.User;

public interface AuthenticationListener {

	void onUserLoggedIn(final User user, String sessionId);

	void onUserLoggedOut(final User user, String sessionId);
}
