package br.com.oncast.ontrack.server.services.persistence;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.notification.Notification;

import java.util.Date;
import java.util.List;

//TODO++++ Rename methods
public interface PersistenceService {

	long persistActions(final UUID projectId, final List<ModelAction> actionList, UUID userId, final Date timestamp) throws PersistenceException;

	ProjectSnapshot retrieveProjectSnapshot(UUID projectId) throws PersistenceException, NoResultFoundException;

	List<UserAction> retrieveActionsSince(UUID projectId, long actionId) throws PersistenceException;

	void persistProjectSnapshot(ProjectSnapshot projectSnapshot) throws PersistenceException;

	User retrieveUserByEmail(String email) throws NoResultFoundException, PersistenceException;

	User retrieveUserById(UUID userId) throws NoResultFoundException, PersistenceException;

	User persistOrUpdateUser(User user) throws PersistenceException;

	List<User> retrieveAllUsers() throws PersistenceException;

	List<Password> retrievePasswordsForUser(UUID userId) throws PersistenceException;

	void persistOrUpdatePassword(Password passwordForUser) throws PersistenceException;

	List<Password> retrieveAllPasswords() throws PersistenceException;

	void remove(Password passw) throws PersistenceException;

	ProjectRepresentation persistOrUpdateProjectRepresentation(final ProjectRepresentation projectRepresentation) throws PersistenceException;

	ProjectRepresentation retrieveProjectRepresentation(final UUID projectId) throws PersistenceException, NoResultFoundException;

	List<ProjectRepresentation> retrieveAllProjectRepresentations() throws PersistenceException;

	void authorize(String userEmail, UUID projectId) throws PersistenceException;

	List<ProjectAuthorization> retrieveProjectAuthorizations(final UUID userId) throws PersistenceException;

	List<ProjectAuthorization> retrieveAllProjectAuthorizations() throws PersistenceException;

	/**
	 * Returns a project authorization between an user and a project.
	 * 
	 * @param userId
	 *            the user id.
	 * @param projectId
	 *            the project id.
	 * @return the project authorization if found, <tt>null</tt> otherwise.
	 * @throws PersistenceException
	 *             in case persistence layer fails.
	 */
	ProjectAuthorization retrieveProjectAuthorization(UUID userId, UUID projectId) throws PersistenceException;

	void persistOrUpdateFileRepresentation(FileRepresentation fileRepresentation) throws PersistenceException;

	FileRepresentation retrieveFileRepresentationById(UUID fileId) throws NoResultFoundException, PersistenceException;

	List<Notification> retrieveLatestNotificationsForUser(UUID userId, int maxNotifications) throws NoResultFoundException, PersistenceException;

	Notification persistOrUpdateNotification(Notification notification) throws PersistenceException;

	List<User> retrieveUsersOfProject(UUID projectId) throws PersistenceException;

	List<ProjectAuthorization> retrieveProjectAuthorizationsForProject(UUID projectId) throws PersistenceException;

	List<User> retrieveUsersByIds(List<UUID> userIds) throws PersistenceException;

	List<Notification> retrieveLatestNotifications(Date initialDate) throws PersistenceException;

	List<Notification> retrieveLatestProjectNotifications(List<UUID> projectIds, Date initialDate) throws PersistenceException;

	void authorize(UUID userId, UUID projectId) throws PersistenceException;

	void remove(ProjectAuthorization authorization) throws PersistenceException;

	long countActionsSince(Date date) throws PersistenceException;

	List<UserAction> retrieveActionsSince(Date date) throws PersistenceException;

	Date retrieveFirstActionTimestamp(UUID projectId, UUID userId) throws PersistenceException;

	Date retrieveLastActionTimestamp(UUID projectId, UUID userId) throws PersistenceException;

	List<UserAction> retrieveAllTeamInviteActionsAuthoredBy(UUID userId) throws PersistenceException;

	Date retrieveInvitationTimestamp(UUID userId) throws PersistenceException;

	long retrieveAuthoredActionsCount(UUID userId) throws PersistenceException;

	Date retrieveLastActionTimestamp(UUID userId) throws PersistenceException;

	long retrieveAllAuthoredTeamInviteActionsCount(UUID userId) throws PersistenceException;

	UserAction retrieveAction(UUID projectId, UUID id) throws PersistenceException;

}