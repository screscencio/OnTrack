package br.com.oncast.ontrack.server.services.authentication;

import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.email.PasswordResetMailFactory;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.session.Session;
import br.com.oncast.ontrack.server.services.session.SessionManager;
import br.com.oncast.ontrack.shared.exceptions.authentication.AuthenticationException;
import br.com.oncast.ontrack.shared.exceptions.authentication.InvalidAuthenticationCredentialsException;
import br.com.oncast.ontrack.shared.exceptions.authentication.UnableToResetPasswordException;
import br.com.oncast.ontrack.shared.exceptions.authentication.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.PasswordValidator;

// TODO ++++Increment password strength validation, reflecting it on the UI as well so the user can create it without getting bored/angry.
// TODO ++++Review this class method separation.
public class AuthenticationManager {

	private static final Logger LOGGER = Logger.getLogger(AuthenticationManager.class);

	private static final String DEFAULT_NEW_USER_PASSWORD = "";

	private final PersistenceService persistenceService;
	private final SessionManager sessionManager;

	private final Set<AuthenticationListener> authenticationListeners = new HashSet<AuthenticationListener>();

	public AuthenticationManager(final PersistenceService persistenceService, final SessionManager sessionManager) {
		this.persistenceService = persistenceService;
		this.sessionManager = sessionManager;
	}

	public User authenticate(final String email, final String password) throws InvalidAuthenticationCredentialsException {
		final String formattedUserEmail = formatUserEmail(email);
		try {
			final User user = findUserByEmail(formattedUserEmail);
			final List<Password> passwordsForUser = findPasswordForUserOrCreateANewOne(user);

			final Password validUserPassword = getValidUserPassword(password, passwordsForUser);
			if (validUserPassword == null) throw new InvalidAuthenticationCredentialsException(
					"Incorrect password for user with e-mail "
							+ formattedUserEmail);

			for (final Password passw : passwordsForUser)
				if (validUserPassword != passw) persistenceService.remove(passw);

			sessionManager.getCurrentSession().setAuthenticatedUser(user);
			notifyUserLoggedIn(user);
			return user;
		}
		catch (final UserNotFoundException e) {
			throw new InvalidAuthenticationCredentialsException(e);
		}
		catch (final PersistenceException e) {
			final String message = "Could not remove unused user passwords for '" + formattedUserEmail + "'";
			LOGGER.error(message, e);
			throw new RuntimeException();
		}
	}

	public void logout() {
		final Session session = sessionManager.getCurrentSession();
		final User user = session.getAuthenticatedUser();

		session.setAuthenticatedUser(null);
		notifyUserLoggedOut(user);
	}

	public Boolean isUserAuthenticated() {
		return sessionManager.getCurrentSession().getAuthenticatedUser() != null;
	}

	public User getAuthenticatedUser() {
		return sessionManager.getCurrentSession().getAuthenticatedUser();
	}

	public boolean hasUser(final String email) {
		try {
			if (findUserByEmail(email) != null) return true;
		}
		catch (final UserNotFoundException e) {}
		return false;
	}

	public User createNewUser(final String email, final String password, final int projectInvitationQuota, final int projectCreationQuota) {
		return createNewUser(new UUID(), email, password, projectInvitationQuota, projectCreationQuota);
	}

	public User createNewUser(final UUID id, final String email, final String password, final int projectInvitationQuota, final int projectCreationQuota) {
		final String formattedUserEmail = formatUserEmail(email);

		try {
			final User user = new User(id, formattedUserEmail, projectInvitationQuota, projectCreationQuota);
			final User newUser = persistenceService.persistOrUpdateUser(user);
			if (password != null && !password.isEmpty()) createPasswordForUser(newUser, password);
			return newUser;
		}
		catch (final PersistenceException e) {
			final String message = "Could not create a new user with e-mail '" + formattedUserEmail + "'";
			LOGGER.error(message, e);
			throw new AuthenticationException(message);

		}
	}

	public void updateCurrentUserPassword(final String currentPassword, final String newPassword) throws InvalidAuthenticationCredentialsException {
		final String username = sessionManager.getCurrentSession().getAuthenticatedUser().getEmail();
		if (!PasswordValidator.isValid(newPassword)) throw new InvalidAuthenticationCredentialsException("The new given password is invalid.");

		try {
			final User user = findUserByEmail(username);
			final List<Password> userPasswords = findPasswordForUser(user);

			final Password userPassword = getValidUserPassword(currentPassword, userPasswords);
			if (userPassword == null) throw new InvalidAuthenticationCredentialsException(
					"Could not change the password for the user " + username
							+ ", because the current password is incorrect.");

			userPassword.setPassword(newPassword);
			persistenceService.persistOrUpdatePassword(userPassword);
		}
		catch (final UserNotFoundException e) {
			final String message = "Unable to update the user '" + username + "'s password: no user was found for this email.";
			LOGGER.error(message, e);
			throw new AuthenticationException(message);
		}
		catch (final PersistenceException e) {
			final String message = "Unable to update the user '" + username + "'s password: it was not possible to persist it.";
			LOGGER.error(message, e);
			throw new AuthenticationException(message);
		}
	}

	private Password getValidUserPassword(final String password, final List<Password> userPasswords) {
		for (final Password pass : userPasswords)
			if (pass.authenticate(password)) return pass;
		return null;
	}

	public void register(final AuthenticationListener authenticationListener) {
		authenticationListeners.add(authenticationListener);
	}

	public void unregister(final AuthenticationListener authenticationListener) {
		authenticationListeners.remove(authenticationListener);
	}

	public User findUserByEmail(final String email) throws UserNotFoundException {
		User user;

		try {
			user = persistenceService.retrieveUserByEmail(email);
		}
		catch (final NoResultFoundException e) {
			throw new UserNotFoundException("No user found with e-mail '" + email + "'.");
		}
		catch (final PersistenceException e) {
			final String message = "Unable to find user by email '" + email + "'.";
			LOGGER.error(message, e);
			throw new AuthenticationException(message);
		}
		return user;
	}

	private Password createPasswordForUser(final User user, final String password) {
		try {
			final Password newPassword = new Password(user.getId(), password);
			persistenceService.persistOrUpdatePassword(newPassword);
			return newPassword;
		}
		catch (final PersistenceException e) {
			final String message = "Could not create a password for the user with e-mail " + user.getEmail();
			LOGGER.error(message, e);
			throw new AuthenticationException(message);
		}
	}

	private List<Password> findPasswordForUser(final User user) {
		try {
			return persistenceService.retrievePasswordsForUser(user.getId());
		}
		catch (final PersistenceException e) {
			LOGGER.error("Unable to find the password for user " + user.getEmail(), e);
			throw new AuthenticationException();
		}
	}

	// TODO Fix - creating a new empty password if the user doesn't have one...
	// TODO Review this method when user account creation is implemented.
	private List<Password> findPasswordForUserOrCreateANewOne(final User user) throws UserNotFoundException {
		try {
			final List<Password> passwordsForUser = persistenceService.retrievePasswordsForUser(user.getId());
			if (passwordsForUser.isEmpty()) {
				passwordsForUser.add(createPasswordForUser(user, DEFAULT_NEW_USER_PASSWORD));
			}
			return passwordsForUser;
		}
		catch (final PersistenceException e) {
			LOGGER.error("Unable to find passowrd for user.", e);
			throw new AuthenticationException();
		}
	}

	// TODO ++Extract this method to a external class, responsible for formatting the user email, which can then be used both in client and server.
	private String formatUserEmail(final String email) {
		final String formattedUserEmail = email.toLowerCase().trim();
		return formattedUserEmail;
	}

	private void notifyUserLoggedIn(final User user) {
		final String sessionId = sessionManager.getCurrentSession().getSessionId();
		for (final AuthenticationListener listener : authenticationListeners)
			listener.onUserLoggedIn(user, sessionId);
	}

	private void notifyUserLoggedOut(final User user) {
		final String sessionId = sessionManager.getCurrentSession().getSessionId();
		for (final AuthenticationListener listener : authenticationListeners)
			listener.onUserLoggedOut(user, sessionId);
	}

	public void resetPassword(final String username) throws UnableToResetPasswordException, InvalidAuthenticationCredentialsException {
		try {
			final User user = findUserByEmail(username);
			final String newPassword = PasswordHash.generatePassword();
			createPasswordForUser(user, newPassword);
			new PasswordResetMailFactory().createMail().send(username, newPassword);
		}
		catch (final UserNotFoundException e) {
			final String message = "Unable to update the user '" + username + "'s password: no user was found for this email.";
			LOGGER.error(message, e);
			throw new InvalidAuthenticationCredentialsException(message);
		}
		catch (final NoSuchAlgorithmException e) {
			final String message = "Unable to update the user '" + username + "'s password: new password could not be created.";
			LOGGER.error(message, e);
			throw new UnableToResetPasswordException(message);
		}
	}
}
