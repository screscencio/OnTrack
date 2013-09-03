package br.com.oncast.ontrack.server.services.authorization;

import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.authorization.PermissionDeniedException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToAuthorizeUserException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToRemoveAuthorizationException;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.Profile;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.List;

public interface AuthorizationManager {

	UUID authorize(final UUID projectId, final String userEmail, final Profile profile, boolean shouldSendMailMessage) throws UnableToAuthorizeUserException, PermissionDeniedException;

	void authorizeAdmin(final ProjectRepresentation persistedProjectRepresentation) throws PersistenceException;

	void assureActiveProjectAccessAuthorization(final UUID projectId) throws AuthorizationException;

	void assureProjectAccessAuthorizationEvenRemovedOnes(UUID projectId) throws AuthorizationException;

	void validateSuperUser(UUID userId) throws PermissionDeniedException;

	List<ProjectRepresentation> listAuthorizedProjects(UUID userId) throws PersistenceException, NoResultFoundException;

	boolean hasAuthorizationFor(UUID userId, UUID projectId) throws NoResultFoundException, PersistenceException;

	void removeAuthorization(UUID projectId, UUID userId) throws UnableToRemoveAuthorizationException;

}
