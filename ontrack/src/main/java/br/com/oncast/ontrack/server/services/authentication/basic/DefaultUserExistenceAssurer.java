package br.com.oncast.ontrack.server.services.authentication.basic;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.user.User;

public class DefaultUserExistenceAssurer {

	private static final Logger LOGGER = Logger.getLogger(DefaultUserExistenceAssurer.class);

	private static final String DEFAULT_USER = DefaultAuthenticationCredentials.USER;
	private static final String DEFAULT_PASSWORD = DefaultAuthenticationCredentials.PASSWORD;

	public static void verify() {
		final PersistenceService persistenceService = ServerServiceProvider.getInstance().getPersistenceService();
		try {
			persistenceService.findUserByEmail(DEFAULT_USER);
		}
		catch (final NoResultFoundException e) {
			createNewUser(persistenceService);
		}
		catch (final PersistenceException e) {
			LOGGER.error("An exception was found while trying to verify the presence of the default user.", e);
			throw new RuntimeException(e);
		}
	}

	private static void createNewUser(final PersistenceService persistenceService) {
		final User newUser = new User();
		newUser.setEmail(DEFAULT_USER);
		try {
			persistenceService.persistOrUpdateUser(newUser);
			final User persistedUser = persistenceService.findUserByEmail(newUser.getEmail());
			verifyUserPassword(persistenceService, persistedUser);
		}
		catch (final PersistenceException e) {
			LOGGER.error("An exception was found while trying to create a new user.", e);
			throw new RuntimeException(e);
		}
		catch (final NoResultFoundException e) {
			LOGGER.error("An exception was found while trying to retrieve the newly created user.", e);
			throw new RuntimeException(e);
		}
	}

	private static void verifyUserPassword(final PersistenceService persistenceService, final User user) throws PersistenceException {
		Password password;
		try {
			password = persistenceService.findPasswordForUser(user.getId());
			verifyPasswordIfExists(persistenceService, password);
		}
		catch (final NoResultFoundException e) {
			createNewPasswordForUser(persistenceService, user);
		}
	}

	private static void verifyPasswordIfExists(final PersistenceService persistenceService, final Password password) throws PersistenceException {
		if (!password.authenticate(DEFAULT_PASSWORD)) {
			password.setPassword(DEFAULT_PASSWORD);
			persistenceService.persistOrUpdatePassword(password);
		}
	}

	private static void createNewPasswordForUser(final PersistenceService persistenceService, final User user) throws PersistenceException {
		Password password;
		password = new Password();
		password.setUserId(user.getId());
		password.setPassword(DEFAULT_PASSWORD);
		persistenceService.persistOrUpdatePassword(password);
	}
}
