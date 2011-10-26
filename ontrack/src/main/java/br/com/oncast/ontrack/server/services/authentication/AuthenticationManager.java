package br.com.oncast.ontrack.server.services.authentication;

import br.com.oncast.ontrack.server.model.Password;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.session.SessionManager;
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
		final Password passwordForUser = findPasswordForUser(user);

		if (!passwordForUser.authenticate(password)) throw new IncorrectPasswordException("Password incorreto!");

		setAuthenticatedUser(user);
		return user;
	}

	public void logout() {
		setAuthenticatedUser(null);
	}

	public void setAuthenticatedUser(final User user) {
		SessionManager.getCurrentSession().setAuthenticatedUser(user);
	}

	public Boolean isCurrentUserLoggedIn() {
		return SessionManager.getCurrentSession().getAuthenticatedUser() != null;
	}

	public void changePasswordForUser(final String email, final String oldPassword, final String newPassword) throws UserNotFoundException,
			IncorrectPasswordException {
		final User user = findUserByEmail(email);
		Password passwordForUser;
		try {
			passwordForUser = findPasswordForUserId(user.getId());
		}
		catch (final NoResultFoundException e1) {
			throw new RuntimeException("Unable to find the password for user: " + email, e1);
		}

		if (passwordForUser.authenticate(oldPassword)) {
			try {
				passwordForUser.setPassword(newPassword);
				persistenceService.persistOrUpdatePassword(passwordForUser);
			}
			catch (final PersistenceException e) {
				throw new RuntimeException(e);
			}
		}
		else throw new IncorrectPasswordException("Could not change the password for the user " + email + ", because the old password is incorrect.");
	}

	private User findUserByEmail(final String email) throws UserNotFoundException {
		User user;

		try {
			user = persistenceService.findUserByEmail(email);
		}
		catch (final NoResultFoundException e) {
			throw new UserNotFoundException("User not found.", e);
		}
		catch (final PersistenceException e) {
			throw new RuntimeException(e);
		}
		return user;
	}

	private Password findPasswordForUser(final User user) throws UserNotFoundException {
		Password passwordForUser;
		try {
			passwordForUser = findPasswordForUserId(user.getId());
		}
		catch (final NoResultFoundException e) {
			// TODO Creating a new empty password if the user don't have one...
			// TODO When the user account is implemented this logic must be removed.
			passwordForUser = createAnEmptyPassword(user.getId());
		}
		return passwordForUser;
	}

	private Password createAnEmptyPassword(final long userId) {
		Password newPassword = null;

		try {
			final Password passwordForUser = new Password();
			passwordForUser.setUserId(userId);
			persistenceService.persistOrUpdatePassword(passwordForUser);

			newPassword = findPasswordForUserId(userId);
		}
		catch (final PersistenceException e1) {
			e1.printStackTrace();
		}
		catch (final NoResultFoundException e) {
			e.printStackTrace();
		}
		return newPassword;
	}

	private Password findPasswordForUserId(final long userId) throws NoResultFoundException {
		Password password = null;

		try {
			password = persistenceService.findPasswordForUserId(userId);
		}
		catch (final PersistenceException e) {
			throw new RuntimeException(e);
		}
		return password;
	}
}
