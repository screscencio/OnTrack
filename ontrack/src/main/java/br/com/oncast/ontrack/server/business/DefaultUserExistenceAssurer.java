package br.com.oncast.ontrack.server.business;

import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;

public class DefaultUserExistenceAssurer {

	private static final AuthenticationManager AUTHENTICATION_MANAGER = ServerServiceProvider.getInstance().getAuthenticationManager();

	// TODO Analyze if the default user/password should be admin/admin to be more intuitive. (then the administrator sets his own password after first access)
	private static final String DEFAULT_USER = DefaultAuthenticationCredentials.USER;
	private static final String DEFAULT_PASSWORD = DefaultAuthenticationCredentials.PASSWORD;

	public static void verify() {
		if (!AUTHENTICATION_MANAGER.hasUser(DEFAULT_USER)) AUTHENTICATION_MANAGER.createNewUser(DEFAULT_USER, DEFAULT_PASSWORD);
	}
}
