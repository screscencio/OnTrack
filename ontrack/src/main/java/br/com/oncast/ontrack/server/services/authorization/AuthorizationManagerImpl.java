package br.com.oncast.ontrack.server.services.authorization;

import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.server.services.authentication.PasswordHash;
import br.com.oncast.ontrack.server.services.email.MailFactory;
import br.com.oncast.ontrack.server.services.integration.IntegrationService;
import br.com.oncast.ontrack.server.services.multicast.ClientManager;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.shared.exceptions.authentication.UserNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.authorization.PermissionDeniedException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToAuthorizeUserException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToRemoveAuthorizationException;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.context.ProjectAddedEvent;
import br.com.oncast.ontrack.shared.services.context.ProjectRemovedEvent;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class AuthorizationManagerImpl implements AuthorizationManager {

	private static final Logger LOGGER = Logger.getLogger(AuthorizationManagerImpl.class);
	private final AuthenticationManager authenticationManager;
	private final PersistenceService persistenceService;
	private final MulticastService multicastService;
	private final MailFactory mailFactory;
	private final ClientManager clientManager;
	private final IntegrationService integrationService;

	public AuthorizationManagerImpl(final AuthenticationManager authenticationManager, final PersistenceService persistenceService, final MulticastService multicastService,
			final MailFactory mailFactory, final ClientManager clientManager, final IntegrationService integrationMock) {
		this.authenticationManager = authenticationManager;
		this.persistenceService = persistenceService;
		this.multicastService = multicastService;
		this.mailFactory = mailFactory;
		this.clientManager = clientManager;
		this.integrationService = integrationMock;
	}

	@Override
	public void assureProjectAccessAuthorization(final UUID projectId) throws AuthorizationException {
		final UUID currentUserId = getAuthenticatedUserOrAdmin().getId();
		ProjectAuthorization retrieveProjectAuthorization;
		try {
			retrieveProjectAuthorization = persistenceService.retrieveProjectAuthorization(currentUserId, projectId);
			if (retrieveProjectAuthorization == null) throw new AuthorizationException("Not authorized to access project '" + projectId + "'.").setProjectId(projectId);
		} catch (final PersistenceException e) {
			throw new AuthorizationException("Could not check project authorization for project '" + projectId + "': Persistence error.", e).setProjectId(projectId);
		}

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
	public UUID authorize(final UUID projectId, final String userEmail, final boolean isSuperUser, final boolean shouldSendMailMessage) throws UnableToAuthorizeUserException,
			PermissionDeniedException {
		User user = null;

		try {
			final User authenticatedUser = getAuthenticatedUserOrAdmin();
			String generatedPassword = null;

			try {
				user = authenticationManager.findUserByEmail(userEmail);

				if (persistenceService.retrieveProjectAuthorization(user.getId(), projectId) != null) {
					logAndThrowUnableToAuthorizeUserException("The user '" + userEmail + "' is already authorized for the project '" + projectId + "'");
				}
			} catch (final UserNotFoundException e) {
				generatedPassword = PasswordHash.generatePassword();
				user = authenticationManager.createNewUser(userEmail, generatedPassword, isSuperUser);
				LOGGER.debug("Created New User '" + userEmail + "'.");
			}

			persistenceService.authorize(userEmail, projectId);
			final ProjectRepresentation projectRepresentation = persistenceService.retrieveProjectRepresentation(projectId);
			multicastService.multicastToUser(new ProjectAddedEvent(projectRepresentation), user);
			integrationService.onUserInvited(projectId, authenticatedUser, user, isSuperUser);
			if (shouldSendMailMessage) sendMailMessage(projectId, userEmail, generatedPassword, authenticatedUser);

		} catch (final PersistenceException e) {
			logAndThrowUnableToAuthorizeUserException("It was not possible to authorize the user '" + userEmail + "' for the project.", e);
		} catch (final NoSuchAlgorithmException e) {
			logAndThrowUnableToAuthorizeUserException("It was not possible to authorize the user '" + userEmail + "' for the project: Password generation went wrong.", e);
		} catch (final NoResultFoundException e) {
			logAndThrowUnableToAuthorizeUserException(
					"It was not possible to authorize the user '" + userEmail + "' for the project: Trying to authorize to an inexistent project or inexistent user.", e);
		}
		return user.getId();

	}

	private User getAuthenticatedUserOrAdmin() {
		return authenticationManager.isUserAuthenticated() ? authenticationManager.getAuthenticatedUser() : new User(DefaultAuthenticationCredentials.USER_ID,
				DefaultAuthenticationCredentials.USER_EMAIL);
	}

	private void sendMailMessage(final UUID projectId, final String userEmail, final String generatedPassword, final User authenticatedUser) {
		try {
			mailFactory.createProjectAuthorizationMail().currentUser(authenticatedUser.getEmail()).setProject(persistenceService.retrieveProjectRepresentation(projectId))
					.sendTo(userEmail, generatedPassword);
		} catch (final Exception e) {
			LOGGER.error("It was not possible to send e-mail.", e);
		}
	}

	@Override
	public void validateCanCreateProject(final UUID userId) throws PermissionDeniedException {
		String message = "Authorized!";
		try {
			final User user = persistenceService.retrieveUserById(userId);
			if (user.isSuperUser()) return;

			message = "The current user don't have the permission to do this operation.";
		} catch (final Exception e) {
			message = "An error occured while trying to check user permissions: " + e.getMessage();
		}
		LOGGER.error(message);
		throw new PermissionDeniedException(message);
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
		} catch (final PersistenceException e) {
			logAndThrowUnableToRemoveAuthorizationException("Remove authorization failed: persistence error", e);
		} catch (final NoResultFoundException e) {
			logAndThrowUnableToRemoveAuthorizationException("Remove authorization failed: user or project not found", e);
		}
	}

}
