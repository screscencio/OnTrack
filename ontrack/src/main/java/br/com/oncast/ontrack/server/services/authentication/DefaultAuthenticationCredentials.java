package br.com.oncast.ontrack.server.services.authentication;

import br.com.oncast.ontrack.server.configuration.Configurations;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class DefaultAuthenticationCredentials {

	public static String USER_EMAIL = Configurations.get().getAdminUsername();

	public static String USER_PASSWORD = Configurations.get().getAdminPassword();

	public static UUID USER_ID = new UUID(USER_EMAIL);

}