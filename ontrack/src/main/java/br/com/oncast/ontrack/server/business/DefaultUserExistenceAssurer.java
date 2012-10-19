package br.com.oncast.ontrack.server.business;

import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class DefaultUserExistenceAssurer {

	private static final AuthenticationManager AUTHENTICATION_MANAGER = ServerServiceProvider.getInstance().getAuthenticationManager();

	private static final String DEFAULT_USER = DefaultAuthenticationCredentials.USER_EMAIL;
	private static final String DEFAULT_PASSWORD = DefaultAuthenticationCredentials.USER_PASSWORD;
	private static final UUID DEFAULT_ID = DefaultAuthenticationCredentials.USER_ID;
	private static final int DEFAULT_USER_PROJECT_CREATION_QUOTA = 10;
	private static final int DEFAULT_USER_PROJECT_INVITATION_QUOTA = 10;

	public static void verify() {
		if (!AUTHENTICATION_MANAGER.hasUser(DEFAULT_USER)) AUTHENTICATION_MANAGER.createNewUser(DEFAULT_ID, DEFAULT_USER, DEFAULT_PASSWORD,
				DEFAULT_USER_PROJECT_INVITATION_QUOTA, DEFAULT_USER_PROJECT_CREATION_QUOTA);
	}
}
