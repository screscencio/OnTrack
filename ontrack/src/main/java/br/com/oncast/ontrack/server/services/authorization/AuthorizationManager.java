package br.com.oncast.ontrack.server.services.authorization;

import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToAuthorizeUserException;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface AuthorizationManager {

	public User authorize(final UUID projectId, final String userEmail, final boolean shouldSendMailMessage)
			throws UnableToAuthorizeUserException;

	public void authorizeAdmin(final ProjectRepresentation persistedProjectRepresentation) throws PersistenceException;

	public List<ProjectRepresentation> listAuthorizedProjects(final User user) throws PersistenceException, NoResultFoundException;

	public void assureProjectAccessAuthorization(final UUID projectId) throws PersistenceException, AuthorizationException;

	public void validateAndUpdateUserProjectCreationQuota(User requestingUser) throws PersistenceException, AuthorizationException;

	public List<ProjectRepresentation> listAuthorizedProjects(UUID userId) throws PersistenceException, NoResultFoundException;

	public boolean hasAuthorizationFor(UUID userId, UUID projectId) throws NoResultFoundException, PersistenceException;

}
