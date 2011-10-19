package br.com.oncast.ontrack.server.services.authentication;

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
		if (!user.authenticate(password)) throw new IncorrectPasswordException("Password incorreto!");

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

		if (user.authenticate(oldPassword)) {
			try {
				user.setPassword(newPassword);
				persistenceService.persistOrUpdateUser(user);
			}
			catch (final NoResultFoundException e) {
				throw new UserNotFoundException("User not found.", e);
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
}
