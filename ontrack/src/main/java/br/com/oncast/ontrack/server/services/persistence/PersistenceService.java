package br.com.oncast.ontrack.server.services.persistence;

import java.util.Date;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface PersistenceService {

	public void persistActions(final UUID projectId, final List<ModelAction> actionList, long userId, final Date timestamp)
			throws PersistenceException;

	public ProjectSnapshot retrieveProjectSnapshot(UUID projectId) throws PersistenceException, NoResultFoundException;

	public List<UserAction> retrieveActionsSince(UUID projectId, long actionId) throws PersistenceException;

	public void persistProjectSnapshot(ProjectSnapshot projectSnapshot) throws PersistenceException;

	public User retrieveUserByEmail(String email) throws NoResultFoundException, PersistenceException;

	public User retrieveUserById(long userId) throws NoResultFoundException, PersistenceException;

	public User persistOrUpdateUser(User user) throws PersistenceException;

	public List<User> retrieveAllUsers() throws PersistenceException;

	public Password retrievePasswordForUser(long userId) throws NoResultFoundException, PersistenceException;

	public void persistOrUpdatePassword(Password passwordForUser) throws PersistenceException;

	public List<Password> retrieveAllPasswords() throws PersistenceException;

	public ProjectRepresentation persistOrUpdateProjectRepresentation(final ProjectRepresentation projectRepresentation) throws PersistenceException;

	public ProjectRepresentation retrieveProjectRepresentation(final UUID projectId) throws PersistenceException, NoResultFoundException;

	public List<ProjectRepresentation> retrieveAllProjectRepresentations() throws PersistenceException;

	public void authorize(String userEmail, UUID projectId) throws PersistenceException;

	public List<ProjectAuthorization> retrieveProjectAuthorizations(final long userId) throws PersistenceException;

	public List<ProjectAuthorization> retrieveAllProjectAuthorizations() throws PersistenceException;

	/**
	 * Returns a project authorization between an user and a project.
	 * @param userId the user id.
	 * @param projectId the project id.
	 * @return the project authorization if found, <tt>null</tt> otherwise.
	 * @throws PersistenceException in case persistence layer fails.
	 */
	public ProjectAuthorization retrieveProjectAuthorization(long userId, UUID projectId) throws PersistenceException;

	public void persistOrUpdateFileRepresentation(FileRepresentation fileRepresentation) throws PersistenceException;

	public FileRepresentation retrieveFileRepresentationById(UUID fileId) throws NoResultFoundException, PersistenceException;

	public void persistOrUpdateAnnotation(UUID projectId, UUID subjectId, Annotation annotation) throws PersistenceException;

	public Annotation retrieveAnnotationById(UUID projectId, UUID id) throws NoResultFoundException, PersistenceException;

	public List<Annotation> retrieveAnnotationsFromProjectBySubjectId(UUID projectId, UUID subjectId) throws PersistenceException;

	public Set<UUID> retrieveAnnotatedSubjectIdsFromProject(UUID projectId) throws PersistenceException;
}