package br.com.oncast.ontrack.server.services.authorization;

import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToAuthorizeUserException;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;

public interface AuthorizationManager {

	public void authorize(final long projectId, final String userEmail, final boolean shouldSendMailNotification) throws UnableToAuthorizeUserException;

	public void authorizeAdmin(final ProjectRepresentation persistedProjectRepresentation) throws PersistenceException;

	public List<ProjectRepresentation> listAuthorizedProjects(final User user) throws PersistenceException;

	public void assureProjectAccessAuthorization(final long projectId) throws PersistenceException, AuthorizationException;

	public void validateAndUpdateUserProjectCreationQuota(User requestingUser) throws PersistenceException, AuthorizationException;

	public Set<User> listAuthorizedUsers(long projectId) throws PersistenceException;

}
