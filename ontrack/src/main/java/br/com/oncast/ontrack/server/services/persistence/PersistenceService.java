package br.com.oncast.ontrack.server.services.persistence;

import java.util.Date;
import java.util.List;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;

public interface PersistenceService {

	public void persistActions(final long projectId, long userId, final List<ModelAction> actionList, final Date timestamp)
			throws PersistenceException;

	public ProjectSnapshot retrieveProjectSnapshot(long projectId) throws PersistenceException, NoResultFoundException;

	public List<UserAction> retrieveActionsSince(long projectId, long actionId) throws PersistenceException;

	public void persistProjectSnapshot(ProjectSnapshot projectSnapshot) throws PersistenceException;

	public User retrieveUserByEmail(String email) throws NoResultFoundException, PersistenceException;

	public User retrieveUserById(long userId) throws NoResultFoundException, PersistenceException;

	public User persistOrUpdateUser(User user) throws PersistenceException;

	public List<User> retrieveAllUsers() throws PersistenceException;

	public Password retrievePasswordForUser(long userId) throws NoResultFoundException, PersistenceException;

	public void persistOrUpdatePassword(Password passwordForUser) throws PersistenceException;

	public List<Password> retrieveAllPasswords() throws PersistenceException;

	public ProjectRepresentation persistOrUpdateProjectRepresentation(final ProjectRepresentation projectRepresentation) throws PersistenceException;

	public ProjectRepresentation retrieveProjectRepresentation(final long projectId) throws PersistenceException, NoResultFoundException;

	public List<ProjectRepresentation> retrieveAllProjectRepresentations() throws PersistenceException;

	public void authorize(String userEmail, long projectId) throws PersistenceException;

	public List<ProjectAuthorization> retrieveProjectAuthorizations(final long userId) throws PersistenceException;

	public List<ProjectAuthorization> retrieveAllProjectAuthorizations() throws PersistenceException;

	/**
	 * Returns a project authorization between an user and a project.
	 * @param userId the user id.
	 * @param projectId the project id.
	 * @return the project authorization if found, <tt>null</tt> otherwise.
	 * @throws PersistenceException in case persistence layer fails.
	 */
	public ProjectAuthorization retrieveProjectAuthorization(long userId, long projectId) throws PersistenceException;
}