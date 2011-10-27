package br.com.oncast.ontrack.server.services.setup;

import br.com.oncast.ontrack.server.model.Password;
import br.com.oncast.ontrack.server.services.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.user.User;

public class DefaultUserExistenceAssurer {

	private static final String DEFAULT_USER = "admin@ontrack.com";
	private static final String DEFAULT_PASSWORD = "ontrackpoulain";

	public static void verify() {
		final PersistenceService persistenceService = ServerServiceProvider.getInstance().getPersistenceService();
		try {
			persistenceService.findUserByEmail(DEFAULT_USER);
		}
		catch (final NoResultFoundException e) {
			createNewUser(persistenceService);
		}
		catch (final PersistenceException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}
		catch (final NoResultFoundException e) {
			e.printStackTrace();
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
