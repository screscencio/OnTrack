package br.com.oncast.ontrack.server.business;

import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class DefaultUserExistenceAssurer {

	private static final AuthenticationManager AUTHENTICATION_MANAGER = ServerServiceProvider.getInstance().getAuthenticationManager();

	private static final String DEFAULT_USER = DefaultAuthenticationCredentials.USER_EMAIL;
	private static final String DEFAULT_PASSWORD = DefaultAuthenticationCredentials.USER_PASSWORD;
	private static final UUID DEFAULT_ID = DefaultAuthenticationCredentials.USER_ID;

	public static void verify() {
		if (!AUTHENTICATION_MANAGER.hasUser(DEFAULT_USER)) AUTHENTICATION_MANAGER.createNewUser(DEFAULT_ID, DEFAULT_USER, DEFAULT_PASSWORD, true);
	}
}
