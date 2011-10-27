package br.com.oncast.ontrack.server.services.authentication;

import br.com.oncast.ontrack.server.model.Password;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.session.SessionManager;
import br.com.oncast.ontrack.shared.exceptions.authentication.AuthenticationException;
import br.com.oncast.ontrack.shared.exceptions.authentication.IncorrectPasswordException;
import br.com.oncast.ontrack.shared.exceptions.authentication.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.user.User;

public class AuthenticationManager {

	private final PersistenceService persistenceService;

	public AuthenticationManager(final PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
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
		SessionManager.getCurrentSession().setAuthenticatedUser(user);
	}

	public Boolean isCurrentUserLoggedIn() {
		return SessionManager.getCurrentSession().getAuthenticatedUser() != null;
	}

	public void changePasswordForUser(final String email, final String oldPassword, final String newPassword)
			throws UserNotFoundException, IncorrectPasswordException {

		final User user = findUserByEmail(email);
		final Password userPassword = findPasswordForUser(user);

		if (userPassword.authenticate(oldPassword)) persistNewPassword(newPassword, userPassword);
		else throw new IncorrectPasswordException("Could not change the password for the user " + email + ", because the old password is incorrect.");
	}

	private User findUserByEmail(final String email) throws UserNotFoundException {
		User user;

		try {
			user = persistenceService.findUserByEmail(email);
		}
		catch (final NoResultFoundException e) {
			throw new UserNotFoundException("No user found with e-mail " + email + ".", e);
		}
		catch (final PersistenceException e) {
			throw new AuthenticationException(e);
		}
		return user;
	}

	private Password findPasswordForUserOrCreateANewOne(final User user) throws UserNotFoundException {
		Password passwordForUser;
		try {
			passwordForUser = persistenceService.findPasswordForUser(user.getId());
		}
		catch (final NoResultFoundException e) {
			// TODO Creating a new empty password if the user doesn't have one...
			// TODO When the user account creation is implemented this logic must be removed.
			passwordForUser = createEmptyPassword(user);
		}
		catch (final PersistenceException e) {
			throw new AuthenticationException(e);
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
			throw new AuthenticationException("Could not create an empty password for the user with e-mail " + user.getEmail(), e);
		}
		return newPassword;
	}

	private Password findPasswordForUser(final User user) {
		Password password = null;

		try {
			password = persistenceService.findPasswordForUser(user.getId());
		}
		catch (final NoResultFoundException e) {
			throw new AuthenticationException("Unable to find the password for user " + user.getEmail(), e);
		}
		catch (final PersistenceException e) {
			throw new AuthenticationException(e);
		}
		return password;
	}

	private void persistNewPassword(final String newPassword, final Password userPassword) {
		try {
			userPassword.setPassword(newPassword);
			persistenceService.persistOrUpdatePassword(userPassword);
		}
		catch (final PersistenceException e) {
			throw new AuthenticationException(e);
		}
	}

}
