package br.com.oncast.ontrack.server.services.authorization;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.server.services.authentication.PasswordHash;
import br.com.oncast.ontrack.server.services.email.MailFactory;
import br.com.oncast.ontrack.server.services.multicast.ClientManager;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.shared.exceptions.authentication.UserNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToAuthorizeUserException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToRemoveAuthorizationException;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.authentication.UserInformationChangeEvent;
import br.com.oncast.ontrack.shared.services.context.ProjectAddedEvent;
import br.com.oncast.ontrack.shared.services.context.ProjectRemovedEvent;

public class AuthorizationManagerImpl implements AuthorizationManager {

	private static final Logger LOGGER = Logger.getLogger(AuthorizationManagerImpl.class);
	private final AuthenticationManager authenticationManager;
	private final PersistenceService persistenceService;
	private final MulticastService multicastService;
	private final MailFactory mailFactory;
	private final ClientManager clientManager;

	public AuthorizationManagerImpl(final AuthenticationManager authenticationManager, final PersistenceService persistenceService,
			final MulticastService multicastService, final MailFactory mailFactory, final ClientManager clientManager) {

		this.authenticationManager = authenticationManager;
		this.persistenceService = persistenceService;
		this.multicastService = multicastService;
		this.mailFactory = mailFactory;
		this.clientManager = clientManager;
	}

	@Override
	public void assureProjectAccessAuthorization(final UUID projectId) throws PersistenceException, AuthorizationException {
		final UUID currentUserId = authenticationManager.getAuthenticatedUser().getId();
		final ProjectAuthorization retrieveProjectAuthorization = persistenceService.retrieveProjectAuthorization(currentUserId, projectId);
		if (retrieveProjectAuthorization == null) throw new AuthorizationException("Not authorized to access project '" + projectId + "'.")
				.setProjectId(projectId);

	}

	@Override
	public List<ProjectRepresentation> listAuthorizedProjects(final UUID userId) throws PersistenceException, NoResultFoundException {
		final List<ProjectAuthorization> authorizations = persistenceService.retrieveProjectAuthorizations(userId);
		final List<ProjectRepresentation> projects = new ArrayList<ProjectRepresentation>();

		for (final ProjectAuthorization authorization : authorizations)
			projects.add(persistenceService.retrieveProjectRepresentation(authorization.getProjectId()));

		return projects;
	}

	@Override
	public void authorizeAdmin(final ProjectRepresentation persistedProjectRepresentation) throws PersistenceException {
		persistenceService.authorize(DefaultAuthenticationCredentials.USER_EMAIL, persistedProjectRepresentation.getId());
	}

	@Override
	public UUID authorize(final UUID projectId, final String userEmail, final boolean shouldSendMailMessage)
			throws UnableToAuthorizeUserException {
		User user = null;

		try {
			User authenticatedUser = null;
			String generatedPassword = null;

			try {
				user = authenticationManager.findUserByEmail(userEmail);

				if (persistenceService.retrieveProjectAuthorization(user.getId(), projectId) != null) {
					logAndThrowUnableToAuthorizeUserException("The user '" + userEmail + "' is already authorized for the project '" + projectId + "'");
				}
			}
			catch (final UserNotFoundException e) {
				generatedPassword = PasswordHash.generatePassword();
				user = authenticationManager.createNewUser(userEmail, generatedPassword, 0, 0);
				LOGGER.debug("Created New User '" + userEmail + "'.");

				if (authenticationManager.isUserAuthenticated()) {
					authenticatedUser = authenticationManager.getAuthenticatedUser();
					validateAndUpdateUserUserInvitaionQuota(userEmail, authenticatedUser.getId());
				}
			}

			persistenceService.authorize(userEmail, projectId);
			final ProjectRepresentation projectRepresentation = persistenceService.retrieveProjectRepresentation(projectId);
			multicastService.multicastToUser(new ProjectAddedEvent(projectRepresentation), user);
			if (shouldSendMailMessage) sendMailMessage(projectId, userEmail, generatedPassword, authenticatedUser);

		}
		catch (final PersistenceException e) {
			logAndThrowUnableToAuthorizeUserException("It was not possible to authorize the user '" + userEmail + "' for the project.", e);
		}
		catch (final NoSuchAlgorithmException e) {
			logAndThrowUnableToAuthorizeUserException("It was not possible to authorize the user '" + userEmail
					+ "' for the project: Password generation went wrong.", e);
		}
		catch (final NoResultFoundException e) {
			logAndThrowUnableToAuthorizeUserException("It was not possible to authorize the user '" + userEmail
					+ "' for the project: Trying to authorize to an inexistent project or inexistent user.", e);
		}
		return user.getId();

	}

	void validateAndUpdateUserUserInvitaionQuota(final String userToBeAuthorizedEmail, final UUID userId)
			throws UnableToAuthorizeUserException, PersistenceException, NoResultFoundException {

		final User user = persistenceService.retrieveUserById(userId);

		if (!user.getEmail().equals(userToBeAuthorizedEmail)) {

			final int invitationQuota = user.getProjectInvitationQuota();
			if (invitationQuota <= 0) logAndThrowUnableToAuthorizeUserException("The current user's invitation quota has exceeded.");

			user.setProjectInvitationQuota(invitationQuota - 1);
			persistenceService.persistOrUpdateUser(user);
			multicastService.multicastToUser(new UserInformationChangeEvent(user), user);
		}
	}

	private void sendMailMessage(final UUID projectId, final String userEmail, final String generatedPassword, final User authenticatedUser) {
		try {
			mailFactory.createProjectAuthorizationMail()
					.currentUser(authenticatedUser == null ? DefaultAuthenticationCredentials.USER_EMAIL : authenticatedUser.getEmail())
					.setProject(persistenceService.retrieveProjectRepresentation(projectId)).sendTo(userEmail, generatedPassword);
		}
		catch (final Exception e) {
			LOGGER.error("It was not possible to send e-mail.", e);
		}
	}

	@Override
	public void validateAndUpdateUserProjectCreationQuota(final User requestingUser) throws PersistenceException, AuthorizationException {
		User user = null;
		try {
			user = persistenceService.retrieveUserById(requestingUser.getId());
		}
		catch (final NoResultFoundException e) {
			throw new AuthorizationException("It was not possible to update user project quota. User not found.");
		}
		final int projectCreationQuota = user.getProjectCreationQuota();
		if (projectCreationQuota <= 0) throw new AuthorizationException("The current user's project creation quota has exceeded.");

		user.setProjectCreationQuota(projectCreationQuota - 1);
		persistenceService.persistOrUpdateUser(user);
		multicastService.multicastToUser(new UserInformationChangeEvent(user), user);
	}

	private void logAndThrowUnableToAuthorizeUserException(final String message) throws UnableToAuthorizeUserException {
		LOGGER.error(message);
		throw new UnableToAuthorizeUserException(message);
	}

	private void logAndThrowUnableToAuthorizeUserException(final String message, final Throwable e) throws UnableToAuthorizeUserException {
		LOGGER.error(message, e);
		throw new UnableToAuthorizeUserException(message, e);
	}

	private void logAndThrowUnableToRemoveAuthorizationException(final String message, final Throwable e) throws UnableToRemoveAuthorizationException {
		LOGGER.error(message, e);
		throw new UnableToRemoveAuthorizationException(message, e);
	}

	private void logAndThrowUnableToRemoveAuthorizationException(final String message) throws UnableToRemoveAuthorizationException {
		LOGGER.error(message);
		throw new UnableToRemoveAuthorizationException(message);
	}

	@Override
	public boolean hasAuthorizationFor(final UUID userId, final UUID projectId) throws NoResultFoundException, PersistenceException {
		final User user = persistenceService.retrieveUserById(userId);
		return persistenceService.retrieveProjectAuthorization(user.getId(), projectId) != null;
	}

	@Override
	public void removeAuthorization(final UUID projectId, final UUID userId) throws UnableToRemoveAuthorizationException {
		try {
			final ProjectAuthorization authorization = persistenceService.retrieveProjectAuthorization(userId, projectId);
			if (authorization == null) logAndThrowUnableToRemoveAuthorizationException("Remove authorization failed: persistence error");

			persistenceService.remove(authorization);
			final ProjectRepresentation projectRepresentation = persistenceService.retrieveProjectRepresentation(projectId);
			final User user = persistenceService.retrieveUserById(userId);

			clientManager.unbindUserFromProject(userId, projectId);
			multicastService.multicastToUser(new ProjectRemovedEvent(projectRepresentation), user);
		}
		catch (final PersistenceException e) {
			logAndThrowUnableToRemoveAuthorizationException("Remove authorization failed: persistence error", e);
		}
		catch (final NoResultFoundException e) {
			logAndThrowUnableToRemoveAuthorizationException("Remove authorization failed: user or project not found", e);
		}
	}

}
