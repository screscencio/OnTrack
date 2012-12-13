package br.com.oncast.ontrack.server.services.authorization;

import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToAuthorizeUserException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToRemoveAuthorizationException;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface AuthorizationManager {

	UUID authorize(final UUID projectId, final String userEmail, final boolean shouldSendMailMessage)
			throws UnableToAuthorizeUserException;

	void authorizeAdmin(final ProjectRepresentation persistedProjectRepresentation) throws PersistenceException;

	void assureProjectAccessAuthorization(final UUID projectId) throws PersistenceException, AuthorizationException;

	void validateAndUpdateUserProjectCreationQuota(User requestingUser) throws PersistenceException, AuthorizationException;

	List<ProjectRepresentation> listAuthorizedProjects(UUID userId) throws PersistenceException, NoResultFoundException;

	boolean hasAuthorizationFor(UUID userId, UUID projectId) throws NoResultFoundException, PersistenceException;

	void removeAuthorization(UUID projectId, UUID userId) throws UnableToRemoveAuthorizationException;

}
