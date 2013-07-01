package br.com.oncast.ontrack.server.business;

import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DefaultUserExistenceAssurerTest {

	private static final String DEFAULT_PASSWORD = DefaultAuthenticationCredentials.USER_PASSWORD;
	private static final String DEFAULT_EMAIL = DefaultAuthenticationCredentials.USER_EMAIL;
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
	@Ignore("Ignored because the password is always empty in dev mode and can't mock GWT.isProdMode()")
	public void assertCreatedUserHaveDefinedPassword() {
		DefaultUserExistenceAssurer.verify();
		final User user = findUser(DEFAULT_EMAIL);
		final Password password = findPassword(user.getId());
		assertTrue(password.authenticate(DEFAULT_PASSWORD));
	}

	@Test
	public void ifUserAlreadyExistsDontDoAnything() throws PersistenceException {
		DefaultUserExistenceAssurer.verify();
		final User persistedUser = findUser(DEFAULT_EMAIL);

		final Password password = findPassword(persistedUser.getId());
		password.setPassword("newPassword");

		persistanceService.persistOrUpdatePassword(password);

		DefaultUserExistenceAssurer.verify();

		final Password passwordForUser = findPassword(findUser(DEFAULT_EMAIL).getId());

		assertFalse(passwordForUser.authenticate(DEFAULT_PASSWORD));
		assertTrue(passwordForUser.authenticate("newPassword"));
	}

	private Password findPassword(final UUID userId) {
		Password p = null;
		try {
			p = persistanceService.retrievePasswordsForUser(userId).get(0);
		} catch (final PersistenceException e) {
			fail();
		}
		return p;
	}

	private User findUser(final String email) {
		User user = null;
		try {
			user = persistanceService.retrieveUserByEmail(email);
		} catch (final NoResultFoundException e) {
			fail();
		} catch (final PersistenceException e) {
			fail();
		}
		return user;
	}
}
