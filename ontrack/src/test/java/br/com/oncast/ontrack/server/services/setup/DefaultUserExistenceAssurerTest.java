package br.com.oncast.ontrack.server.services.setup;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.model.Password;
import br.com.oncast.ontrack.server.services.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.user.User;

public class DefaultUserExistenceAssurerTest {

	private static final String DEFAULT_PASSWORD = "ontrackpoulain";
	private static final String DEFAULT_EMAIL = "admin@ontrack.com";
	private PersistenceService persistanceService;

	@Before
	public void setUp() {
		persistanceService = ServerServiceProvider.getInstance().getPersistenceService();
	}

	@Test
	public void verifyMustCreateANewDefaultUserWhenDontFind() {
		DefaultUserExistenceAssurer.verify();
		findUser(DEFAULT_EMAIL);
	}

	@Test
	public void assertCreatedUserHaveDefinedPassword() {
		DefaultUserExistenceAssurer.verify();
		final User user = findUser(DEFAULT_EMAIL);
		final Password password = findPassword(user.getId());
		assertTrue(password.authenticate(DEFAULT_PASSWORD));
	}

	@Test
	public void ifUserAlreadyExistsDontDoAnything() throws PersistenceException {
		final User persistedUser = findUser(DEFAULT_EMAIL);

		final Password password = findPassword(persistedUser.getId());
		password.setPassword("newPassword");

		persistanceService.persistOrUpdatePassword(password);

		DefaultUserExistenceAssurer.verify();

		final Password passwordForUser = findPassword(findUser(DEFAULT_EMAIL).getId());

		assertFalse(passwordForUser.authenticate(DEFAULT_PASSWORD));
		assertTrue(passwordForUser.authenticate("newPassword"));
	}

	private Password findPassword(final long userId) {
		Password p = null;
		try {
			p = persistanceService.findPasswordForUser(userId);
		}
		catch (final NoResultFoundException e) {
			fail();
		}
		catch (final PersistenceException e) {
			fail();
		}
		return p;
	}

	private User findUser(final String email) {
		User user = null;
		try {
			user = persistanceService.findUserByEmail(email);
		}
		catch (final NoResultFoundException e) {
			fail();
		}
		catch (final PersistenceException e) {
			fail();
		}
		return user;
	}
}
