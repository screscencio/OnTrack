package br.com.oncast.ontrack.server.services.authentication;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.session.SessionManager;
import br.com.oncast.ontrack.shared.exceptions.authentication.AuthenticationException;
import br.com.oncast.ontrack.shared.exceptions.authentication.IncorrectPasswordException;
import br.com.oncast.ontrack.shared.exceptions.authentication.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.user.User;

// XXX Auth; Verify password before saving it (extract logic to use in the client "Change Pass" as well); RULEZ: Minimal char = 6;
// TODO Increment password strengh validation, reflecting it on the UI as well so the user can create it without getting bored/angry.
public class AuthenticationManager {

	private static final Logger LOGGER = Logger.getLogger(AuthenticationManager.class);
	private final PersistenceService persistenceService;
	private final SessionManager sessionManager;

	public AuthenticationManager(final PersistenceService persistenceService, final SessionManager sessionManager) {
		this.persistenceService = persistenceService;
		this.sessionManager = sessionManager;
	}

	public User authenticate(final String email, final String password) throws UserNotFoundException, IncorrectPasswordException {
		final User user = findUserByEmail(email);
		final Password passwordForUser = findPasswordForUserOrCreateANewOne(user);

		if (!passwordForUser.authenticate(password)) throw new IncorrectPasswordException("Incorrect password for user with e-mail " + email);

		setAuthenticatedUser(user);
		return user;
	}

	public void logout() {
		setAuthenticatedUser(null);
	}

	private void setAuthenticatedUser(final User user) {
		sessionManager.getCurrentSession().setAuthenticatedUser(user);
	}

	public Boolean isUserAuthenticated() {
		return sessionManager.getCurrentSession().getAuthenticatedUser() != null;
	}

	public User getAuthenticatedUser() {
		return sessionManager.getCurrentSession().getAuthenticatedUser();
	}

	// XXX Auth; Review update password logic. While authentication is not refactored this might fail if the user is not truly authenticated on the server.
	public void updateUserPassword(final String currentPassword, final String newPassword) throws IncorrectPasswordException {
		final String email = sessionManager.getCurrentSession().getAuthenticatedUser().getEmail();

		User user;
		try {
			user = findUserByEmail(email);
		}
		catch (final UserNotFoundException e) {
			// TODO Rewrite method logic.
			throw new RuntimeException(e);
		}

		final Password userPassword = findPasswordForUser(user);

		if (userPassword.authenticate(currentPassword)) persistNewPassword(newPassword, userPassword);
		else throw new IncorrectPasswordException("Could not change the password for the user " + email + ", because the current password is incorrect.");
	}

	private User findUserByEmail(final String email) throws UserNotFoundException {
		User user;

		try {
			user = persistenceService.retrieveUserByEmail(email);
		}
		catch (final NoResultFoundException e) {
			throw new UserNotFoundException("No user found with e-mail " + email + ".", e);
		}
		catch (final PersistenceException e) {
			LOGGER.error("Unable to find user by email.", e);
			throw new AuthenticationException();
		}
		return user;
	}

	private Password findPasswordForUserOrCreateANewOne(final User user) throws UserNotFoundException {
		Password passwordForUser;
		try {
			passwordForUser = persistenceService.retrievePasswordForUser(user.getId());
		}
		catch (final NoResultFoundException e) {
			// TODO Creating a new empty password if the user doesn't have one...
			// TODO When the user account creation is implemented this logic must be removed.
			passwordForUser = createEmptyPassword(user);
		}
		catch (final PersistenceException e) {
			LOGGER.error("Unable to find passowrd for user.", e);
			throw new AuthenticationException();
		}
		return passwordForUser;
	}

	private Password createEmptyPassword(final User user) {
		Password newPassword = null;

		try {
			final Password passwordForUser = new Password();
			passwordForUser.setUserId(user.getId());
			persistenceService.persistOrUpdatePassword(passwordForUser);

			newPassword = findPasswordForUser(user);
		}
		catch (final PersistenceException e) {
			LOGGER.error("Could not create an empty password for the user with e-mail " + user.getEmail(), e);
			throw new AuthenticationException("Could not create an empty password for the user with e-mail " + user.getEmail());
		}
		return newPassword;
	}

	private Password findPasswordForUser(final User user) {
		Password password = null;

		try {
			password = persistenceService.retrievePasswordForUser(user.getId());
		}
		catch (final NoResultFoundException e) {
			LOGGER.error("No password found for user " + user.getEmail(), e);
			throw new AuthenticationException("Unable to find the password for user " + user.getEmail());
		}
		catch (final PersistenceException e) {
			LOGGER.error("Unable to find the password for user " + user.getEmail(), e);
			throw new AuthenticationException();
		}
		return password;
	}

	private void persistNewPassword(final String newPassword, final Password userPassword) {
		try {
			userPassword.setPassword(newPassword);
			persistenceService.persistOrUpdatePassword(userPassword);
		}
		catch (final PersistenceException e) {
			LOGGER.error("Unable to persist the new password for  a user.", e);
			throw new AuthenticationException();
		}
	}

}
