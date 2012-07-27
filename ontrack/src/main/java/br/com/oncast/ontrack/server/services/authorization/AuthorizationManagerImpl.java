package br.com.oncast.ontrack.server.services.authorization;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.server.services.authentication.PasswordHash;
import br.com.oncast.ontrack.server.services.email.ProjectAuthorizationMailFactory;
import br.com.oncast.ontrack.server.services.notification.NotificationService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.shared.exceptions.authentication.UserNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToAuthorizeUserException;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class AuthorizationManagerImpl implements AuthorizationManager {

	private static final Logger LOGGER = Logger.getLogger(AuthorizationManagerImpl.class);
	private final AuthenticationManager authenticationManager;
	private final PersistenceService persistenceService;
	private final NotificationService notificationService;
	private final ProjectAuthorizationMailFactory projectAuthorizationMailFactory;

	public AuthorizationManagerImpl(final AuthenticationManager authenticationManager, final PersistenceService persistenceService,
			final NotificationService notificationService, final ProjectAuthorizationMailFactory projectAuthorizationMailFactory) {

		this.authenticationManager = authenticationManager;
		this.persistenceService = persistenceService;
		this.notificationService = notificationService;
		this.projectAuthorizationMailFactory = projectAuthorizationMailFactory;
	}

	@Override
	public void assureProjectAccessAuthorization(final UUID projectId) throws PersistenceException, AuthorizationException {
		final long currentUserId = authenticationManager.getAuthenticatedUser().getId();
		final ProjectAuthorization retrieveProjectAuthorization = persistenceService.retrieveProjectAuthorization(currentUserId, projectId);
		if (retrieveProjectAuthorization == null) throw new AuthorizationException("Not authorized to access project '" + projectId + "'.");
	}

	@Override
	public List<ProjectRepresentation> listAuthorizedProjects(final User user) throws PersistenceException, NoResultFoundException {
		final List<ProjectAuthorization> authorizations = persistenceService.retrieveProjectAuthorizations(user.getId());
		final List<ProjectRepresentation> projects = new ArrayList<ProjectRepresentation>();

		for (final ProjectAuthorization authorization : authorizations)
			projects.add(persistenceService.retrieveProjectRepresentation(authorization.getProjectId()));

		return projects;
	}

	@Override
	public void authorizeAdmin(final ProjectRepresentation persistedProjectRepresentation) throws PersistenceException {
		final String adminEmail = DefaultAuthenticationCredentials.USER_EMAIL;
		persistenceService.authorize(adminEmail, persistedProjectRepresentation.getId());
	}

	@Override
	// TODO Refactor the code so that even when the system is the authorization requestant an email can be sent. Refactor email builder for that.
	public void authorize(final UUID projectId, final String userEmail, final boolean shouldSendMailNotification) throws UnableToAuthorizeUserException {
		try {
			final String generatedPassword = validateUserAndItsProjectAccessAuthorization(projectId, userEmail);

			if (!authenticationManager.isUserAuthenticated()) {
				persistenceService.authorize(userEmail, projectId);
				return;
			}
			final User authenticatedUser = authenticationManager.getAuthenticatedUser();
			if (generatedPassword != null) validateAndUpdateUserUserInvitaionQuota(userEmail, authenticatedUser);
			persistenceService.authorize(userEmail, projectId);

			if (shouldSendMailNotification) sendMailNotification(projectId, userEmail, generatedPassword, authenticatedUser);
		}
		catch (final PersistenceException e) {
			logAndThrowUnableToAuthorizeUserException("It was not possible to authorize the user '" + userEmail + "' for the project.", e);
		}
		catch (final NoSuchAlgorithmException e) {
			logAndThrowUnableToAuthorizeUserException("It was not possible to authorize the user '" + userEmail
					+ "' for the project: Password generation went wrong.", e);
		}
	}

	private String validateUserAndItsProjectAccessAuthorization(final UUID projectId, final String userEmail) throws PersistenceException,
			UnableToAuthorizeUserException, NoSuchAlgorithmException {
		String generatedPassword = null;
		User user;
		try {
			user = authenticationManager.findUserByEmail(userEmail);
		}
		catch (final UserNotFoundException e) {
			generatedPassword = PasswordHash.generatePassword();
			user = authenticationManager.createNewUser(userEmail, generatedPassword, 0, 0);
			LOGGER.debug("Created New User '" + userEmail + "'.");
		}

		if (persistenceService.retrieveProjectAuthorization(user.getId(), projectId) != null) logAndThrowUnableToAuthorizeUserException("The user '"
				+ userEmail + "' is already authorized for the project '" + projectId + "'");
		return generatedPassword;
	}

	void validateAndUpdateUserUserInvitaionQuota(final String userToBeAuthorizedEmail, final User requestingUser)
			throws UnableToAuthorizeUserException, PersistenceException {
		if (!requestingUser.getEmail().equals(userToBeAuthorizedEmail)) {

			final int invitationQuota = requestingUser.getProjectInvitationQuota();
			if (invitationQuota <= 0) logAndThrowUnableToAuthorizeUserException("The current user's invitation quota has exceeded.");

			requestingUser.setProjectInvitationQuota(invitationQuota - 1);
			persistenceService.persistOrUpdateUser(requestingUser);
			notificationService.notifyUserInformationChange(requestingUser);
		}
	}

	private void sendMailNotification(final UUID projectId, final String userEmail, final String generatedPassword, final User authenticatedUser) {
		try {
			projectAuthorizationMailFactory.createMail().currentUser(authenticatedUser.getEmail())
					.setProject(persistenceService.retrieveProjectRepresentation(projectId)).sendTo(userEmail, generatedPassword);
		}
		catch (final Exception e) {
			LOGGER.error("It was not possible to send e-mail notification", e);
		}
	}

	@Override
	public void validateAndUpdateUserProjectCreationQuota(final User requestingUser) throws PersistenceException, AuthorizationException {
		final int projectCreationQuota = requestingUser.getProjectCreationQuota();
		if (projectCreationQuota <= 0) throw new AuthorizationException("The current user's project creation quota has exceeded.");

		requestingUser.setProjectCreationQuota(projectCreationQuota - 1);
		persistenceService.persistOrUpdateUser(requestingUser);
		notificationService.notifyUserInformationChange(requestingUser);
	}

	private void logAndThrowUnableToAuthorizeUserException(final String message) throws UnableToAuthorizeUserException {
		LOGGER.error(message);
		throw new UnableToAuthorizeUserException(message);
	}

	private void logAndThrowUnableToAuthorizeUserException(final String message, final Throwable e) throws UnableToAuthorizeUserException {
		LOGGER.error(message, e);
		throw new UnableToAuthorizeUserException(message, e);
	}

}
