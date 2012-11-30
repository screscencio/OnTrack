package br.com.oncast.ontrack.server.services.authorization;

import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToAuthorizeUserException;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface AuthorizationManager {

	UserRepresentation authorize(final UUID projectId, final String userEmail, final boolean shouldSendMailMessage)
			throws UnableToAuthorizeUserException;

	void authorizeAdmin(final ProjectRepresentation persistedProjectRepresentation) throws PersistenceException;

	List<ProjectRepresentation> listAuthorizedProjects(final UserRepresentation user) throws PersistenceException, NoResultFoundException;

	void assureProjectAccessAuthorization(final UUID projectId) throws PersistenceException, AuthorizationException;

	void validateAndUpdateUserProjectCreationQuota(UserRepresentation requestingUser) throws PersistenceException, AuthorizationException;

	List<ProjectRepresentation> listAuthorizedProjects(UUID userId) throws PersistenceException, NoResultFoundException;

	boolean hasAuthorizationFor(UUID userId, UUID projectId) throws NoResultFoundException, PersistenceException;

}
