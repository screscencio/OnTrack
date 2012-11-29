package br.com.oncast.ontrack.server.services.persistence.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.UserEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.UserActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.file.FileRepresentationEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.notification.NotificationEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.project.ProjectRepresentationEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.user.PasswordEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.GeneralTypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.notification.Notification;

// TODO ++Extract EntityManager logic to a "EntityManagerManager" (Using a better name).
// TODO Analise using CriteriaApi instead of HQL.
// TODO Implement better exception handling for JPA exceptions
// TODO Separate authentication/authorization persistence methods from business related methods.
// TODO Extract rollback treatment to a method in all the persist methods.
public class PersistenceServiceJpaImpl implements PersistenceService {

	private final static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ontrackPU");
	private final static GeneralTypeConverter TYPE_CONVERTER = new GeneralTypeConverter();

	@Override
	public void persistActions(final UUID projectId, final List<ModelAction> actionList, final UUID userId, final Date timestamp) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			em.getTransaction().begin();
			final ProjectRepresentationEntity projectRepresentationEntity = convertProjectRepresentationToEntity(retrieveProjectRepresentation(projectId));
			for (final ModelAction modelAction : actionList) {
				final ModelActionEntity entity = convertActionToEntity(modelAction);
				final UserActionEntity container = new UserActionEntity(entity, userId.toStringRepresentation(), projectRepresentationEntity, timestamp);
				em.persist(container);
			}
			em.getTransaction().commit();
		}
		catch (final Exception e) {
			try {
				em.getTransaction().rollback();
			}
			catch (final Exception f) {
				e.printStackTrace();
				throw new PersistenceException("It was not possible to neither persist a group of actions nor to rollback it.", f);
			}
			throw new PersistenceException("It was not possible to persist a group of actions.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<UserAction> retrieveActionsSince(final UUID projectId, final long actionId) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select action from " + UserActionEntity.class.getSimpleName()
					+ " as action where action.projectRepresentation.id = :projectId and action.id > :lastActionId order by action.id asc");

			query.setParameter("projectId", projectId.toStringRepresentation());
			query.setParameter("lastActionId", actionId);
			final List<UserActionEntity> actions = query.getResultList();

			return (List<UserAction>) TYPE_CONVERTER.convert(actions);
		}
		catch (final TypeConverterException e) {
			throw new PersistenceException("It was not possible to convert actions.", e);
		}
		catch (final Exception e) {
			throw new PersistenceException("It was not possible to retrieve the project actions.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public void persistProjectSnapshot(final ProjectSnapshot projectSnapshot) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(projectSnapshot);
			em.getTransaction().commit();
		}
		catch (final Exception e) {
			try {
				em.getTransaction().rollback();
			}
			catch (final Exception f) {
				e.printStackTrace();
				throw new PersistenceException("It was not possible to persist the project snapshot nor to rollback it.", f);
			}
			throw new PersistenceException("It was not possible to persist the project snapshot.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public ProjectSnapshot retrieveProjectSnapshot(final UUID projectId) throws PersistenceException, NoResultFoundException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select snapshot from " + ProjectSnapshot.class.getSimpleName()
					+ " as snapshot where snapshot.id = :projectId");
			query.setParameter("projectId", projectId.toStringRepresentation());
			return (ProjectSnapshot) query.getSingleResult();
		}
		catch (final NoResultException e) {
			throw new NoResultFoundException("No snapshot found.", e);
		}
		catch (final Exception e) {
			throw new PersistenceException("It was not possible to retrieve the project snapshot.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public User persistOrUpdateUser(final User user) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final UserEntity entity = (UserEntity) TYPE_CONVERTER.convert(user);
			em.getTransaction().begin();
			final UserEntity mergedUser = em.merge(entity);
			em.getTransaction().commit();
			return (User) TYPE_CONVERTER.convert(mergedUser);
		}
		catch (final Exception e) {
			try {
				em.getTransaction().rollback();
			}
			catch (final Exception f) {
				e.printStackTrace();
				throw new PersistenceException("It was not possible to persist the user nor to rollback it.", f);
			}
			throw new PersistenceException("It was not possible to persist the user.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public User retrieveUserByEmail(final String email) throws NoResultFoundException, PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select user from " + UserEntity.class.getSimpleName() + " as user where user.email = :email");
			query.setParameter("email", email);

			return (User) TYPE_CONVERTER.convert(query.getSingleResult());
		}
		catch (final NoResultException e) {
			throw new NoResultFoundException("No user found with e-mail: " + email, e);
		}
		catch (final Exception e) {
			throw new PersistenceException("It was not possible to retrieve the user.", e);
		}
		finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> retrieveUsersByIds(final List<UUID> userIds) throws PersistenceException {
		List<String> userList = null;
		try {
			userList = (List<String>) TYPE_CONVERTER.convert(userIds);
		}
		catch (final TypeConverterException e1) {
			e1.printStackTrace();
		}

		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("SELECT user FROM " + UserEntity.class.getSimpleName() + " AS user WHERE user.id IN (:ids)");
			query.setParameter("ids", userList);

			return (List<User>) TYPE_CONVERTER.convert(query.getResultList());
		}
		catch (final Exception e) {
			throw new PersistenceException("It was not possible to retrieve the requested users.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public User retrieveUserById(final UUID id) throws NoResultFoundException, PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select user from " + UserEntity.class.getSimpleName() + " as user where user.id = :id");
			query.setParameter("id", id.toStringRepresentation());

			return (User) TYPE_CONVERTER.convert(query.getSingleResult());
		}
		catch (final NoResultException e) {
			throw new NoResultFoundException("No user found with id: " + id, e);
		}
		catch (final Exception e) {
			throw new PersistenceException("It was not possible to retrieve the user.", e);
		}
		finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> retrieveAllUsers() throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select user from " + UserEntity.class.getSimpleName() + " as user");

			return (List<User>) TYPE_CONVERTER.convert(query.getResultList());
		}
		catch (final Exception e) {
			throw new PersistenceException("It was not possible to retrieve users.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public void persistOrUpdatePassword(final Password passwordForUser) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final PasswordEntity passwordEntity = (PasswordEntity) TYPE_CONVERTER.convert(passwordForUser);
			em.getTransaction().begin();
			em.merge(passwordEntity);
			em.getTransaction().commit();
		}
		catch (final Exception e) {
			try {
				em.getTransaction().rollback();
			}
			catch (final Exception f) {
				e.printStackTrace();
				throw new PersistenceException("It was not possible to persist the password nor to rollback it.", f);
			}
			throw new PersistenceException("It was not possible to persist the password.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public Password retrievePasswordForUser(final UUID userId) throws NoResultFoundException, PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select password from " + PasswordEntity.class.getSimpleName() + " as password where password.userId = :userId");
			query.setParameter("userId", userId.toStringRepresentation());

			return convertEntityToPassword((PasswordEntity) query.getSingleResult());
		}
		catch (final NoResultException e) {
			throw new NoResultFoundException("No password found for userId: " + userId, e);
		}
		catch (final Exception e) {
			throw new PersistenceException("It was not possible to retrieve the password for userId: " + userId, e);
		}
		finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Password> retrieveAllPasswords() throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select password from " + PasswordEntity.class.getSimpleName() + " as password");

			final List<PasswordEntity> passwords = query.getResultList();

			return (List<Password>) TYPE_CONVERTER.convert(passwords);
		}
		catch (final Exception e) {
			throw new PersistenceException("It was not possible to retrieve passwords.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public ProjectRepresentation persistOrUpdateProjectRepresentation(final ProjectRepresentation projectRepresentation) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			em.getTransaction().begin();
			em.merge((ProjectRepresentationEntity) TYPE_CONVERTER.convert(projectRepresentation));
			em.getTransaction().commit();
			// TODO ++++ Make this method void, because it is already changing the incoming object with generated id.
			return projectRepresentation;
		}
		catch (final Exception e) {
			try {
				em.getTransaction().rollback();
			}
			catch (final Exception f) {
				e.printStackTrace();
				throw new PersistenceException("It was not possible to persist the project representation nor to rollback it.", f);
			}
			throw new PersistenceException("It was not possible to persist the project representation.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public ProjectRepresentation retrieveProjectRepresentation(final UUID projectId) throws PersistenceException, NoResultFoundException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select projectRepresentation from " + ProjectRepresentation.class.getSimpleName()
					+ " as projectRepresentation where projectRepresentation.id = :projectId");

			query.setParameter("projectId", projectId.toStringRepresentation());
			return (ProjectRepresentation) TYPE_CONVERTER.convert(query.getSingleResult());
		}
		catch (final NoResultException e) {
			throw new NoResultFoundException("No project representation with id '" + projectId + "' was found.", e);
		}
		catch (final Exception e) {
			throw new PersistenceException("It was not possible to retrieve the project representation with id '" + projectId + "'.", e);
		}
		finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectRepresentation> retrieveAllProjectRepresentations() throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select projectRepresentation from " + ProjectRepresentation.class.getSimpleName()
					+ " as projectRepresentation");

			return (List<ProjectRepresentation>) TYPE_CONVERTER.convert(query.getResultList());
		}
		catch (final Exception e) {
			throw new PersistenceException("It was not possible to retrieve the project representations", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	// TODO Consider renaming this to persistProjectAuthorization and change its parameter to receive a ProjectAuthorization.
	public void authorize(final String userEmail, final UUID projectId) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			em.getTransaction().begin();
			final ProjectRepresentation representation = retrieveProjectRepresentation(projectId);
			final User user = retrieveUserByEmail(userEmail);
			final ProjectAuthorization authorization = new ProjectAuthorization(user, representation);
			em.persist(authorization);
			em.getTransaction().commit();
		}
		catch (final Exception e) {
			try {
				em.getTransaction().rollback();
			}
			catch (final Exception f) {
				e.printStackTrace();
				throw new PersistenceException("It was not possible to persist the project authorization nor to rollback it.", f);
			}
			throw new PersistenceException("It was not possible to persist the project authorization.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	// TODO Consider renaming this to persistProjectAuthorization and change its parameter to receive a ProjectAuthorization.
	public void authorize(final UUID userId, final UUID projectId) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			em.getTransaction().begin();
			final ProjectRepresentation representation = retrieveProjectRepresentation(projectId);
			final User user = retrieveUserById(userId);
			final ProjectAuthorization authorization = new ProjectAuthorization(user, representation);
			em.persist(authorization);
			em.getTransaction().commit();
		}
		catch (final Exception e) {
			try {
				em.getTransaction().rollback();
			}
			catch (final Exception f) {
				e.printStackTrace();
				throw new PersistenceException("It was not possible to persist the project authorization nor to rollback it.", f);
			}
			throw new PersistenceException("It was not possible to persist the project authorization.", e);
		}
		finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectAuthorization> retrieveProjectAuthorizations(final UUID userId) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select authorization from " + ProjectAuthorization.class.getSimpleName()
					+ " as authorization where authorization.userId = :userId");
			query.setParameter("userId", userId.toStringRepresentation());
			return query.getResultList();
		}
		catch (final Exception e) {
			throw new PersistenceException("It was not possible to retrieve the project representations", e);
		}
		finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectAuthorization> retrieveAllProjectAuthorizations() throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select authorization from " + ProjectAuthorization.class.getSimpleName() + " as authorization");
			return query.getResultList();
		}
		catch (final Exception e) {
			throw new PersistenceException("It was not possible to retrieve project authorizations", e);
		}
		finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectAuthorization> retrieveAllAuthorizationsForProject(final ProjectRepresentation projectRepresentation) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("SELECT authorization FROM " + ProjectAuthorization.class.getSimpleName()
					+ " AS authorization WHERE authorization.projectId = :projectId");
			query.setParameter("projectId", projectRepresentation.getId().toStringRepresentation());
			return query.getResultList();
		}
		catch (final Exception e) {
			throw new PersistenceException("It was not possible to retrieve this project's authorizations", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public ProjectAuthorization retrieveProjectAuthorization(final UUID userId, final UUID projectId) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select authorization from " + ProjectAuthorization.class.getSimpleName()
					+ " as authorization where authorization.userId = :userId and authorization.projectId = :projectId");
			query.setParameter("userId", userId.toStringRepresentation());
			query.setParameter("projectId", projectId.toStringRepresentation());
			return (ProjectAuthorization) query.getSingleResult();
		}
		catch (final NoResultException e) {
			return null;
		}
		catch (final Exception e) {
			throw new PersistenceException("It was not possible to retrieve the project representations", e);
		}
		finally {
			em.close();
		}
	}

	private Password convertEntityToPassword(final PasswordEntity passwordEntity) throws PersistenceException {
		final Password password;
		try {
			password = (Password) TYPE_CONVERTER.convert(passwordEntity);
		}
		catch (final TypeConverterException e) {
			throw new PersistenceException("It was not possible to convert the passwordEntity to its model", e);
		}
		return password;
	}

	private ModelActionEntity convertActionToEntity(final ModelAction modelAction) throws PersistenceException {
		ModelActionEntity entity;
		try {
			entity = (ModelActionEntity) TYPE_CONVERTER.convert(modelAction);
		}
		catch (final TypeConverterException e) {
			throw new PersistenceException("It was not possible to convert the action to its entity", e);
		}
		return entity;
	}

	private ProjectRepresentationEntity convertProjectRepresentationToEntity(final ProjectRepresentation representation) throws PersistenceException {
		ProjectRepresentationEntity entity;
		try {
			entity = (ProjectRepresentationEntity) TYPE_CONVERTER.convert(representation);
		}
		catch (final TypeConverterException e) {
			throw new PersistenceException("It was not possible to convert the projectRepresentation to its entity", e);
		}
		return entity;
	}

	@Override
	public void persistOrUpdateFileRepresentation(final FileRepresentation fileRepresentation) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			em.getTransaction().begin();

			final FileRepresentationEntity entity = (FileRepresentationEntity) TYPE_CONVERTER.convert(fileRepresentation);
			em.merge(entity);

			em.getTransaction().commit();
		}
		catch (final TypeConverterException e) {
			throw new PersistenceException("It was not possible to convert the FileRepresentation to its entity", e);
		}
		catch (final Exception e) {
			try {
				em.getTransaction().rollback();
			}
			catch (final Exception f) {
				e.printStackTrace();
				throw new PersistenceException("It was not possible to persist the user nor to rollback it.", f);
			}
			throw new PersistenceException("It was not possible to persist the fileRepresentation.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public FileRepresentation retrieveFileRepresentationById(final UUID fileId) throws NoResultFoundException, PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select fileRepresentation from " + FileRepresentationEntity.class.getSimpleName()
					+ " as fileRepresentation where fileRepresentation.id = :id");
			query.setParameter("id", fileId.toStringRepresentation());

			return (FileRepresentation) TYPE_CONVERTER.convert(query.getSingleResult());
		}
		catch (final NoResultException e) {
			throw new NoResultFoundException("No file representation found for id: " + fileId.toStringRepresentation(), e);
		}
		catch (final Exception e) {
			throw new PersistenceException("It was not possible to convert the FileRepresentationEntity to it's model", e);
		}
		finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Notification> retrieveLatestNotificationsForUser(final UserRepresentation user, final int maxNotifications) throws NoResultFoundException,
			PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query queryNotification = em.createQuery("SELECT n FROM " + NotificationEntity.class.getSimpleName()
					+ " n, IN (n.recipients) recipient WHERE recipient.userId = :user ORDER BY n.timestamp DESC");
			queryNotification.setParameter("user", user.getId().toString());
			queryNotification.setMaxResults(maxNotifications);
			final List<NotificationEntity> resultList = queryNotification.getResultList();

			return (List<Notification>) TYPE_CONVERTER.convert(resultList);
		}
		catch (final NoResultException e) {
			throw new NoResultFoundException("No notification found for user: " + user.getId(), e);
		}
		catch (final TypeConverterException e) {
			throw new PersistenceException("It was not possible to convert the NotificationEntity to it's model equivalent.", e);
		}
		finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Notification> retrieveLatestNotifications(final Date initialDate) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query queryNotification = em.createQuery("SELECT n FROM " + NotificationEntity.class.getSimpleName()
					+ " n WHERE n.timestamp > :initialdate ORDER BY n.timestamp DESC");
			queryNotification.setParameter("initialdate", initialDate);
			final List<NotificationEntity> resultList = queryNotification.getResultList();

			return (List<Notification>) TYPE_CONVERTER.convert(resultList);
		}
		catch (final TypeConverterException e) {
			throw new PersistenceException("It was not possible to convert the NotificationEntity to it's model equivalent.", e);
		}
		catch (final Exception e) {
			throw new PersistenceException("Not able to retrieve notifications.", e);
		}
		finally {
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Notification> retrieveLatestProjectNotifications(final List<UUID> projectIds, final Date initialDate) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final List<String> projectStringIds = getStringListFromUUIDList(projectIds);

			final Query queryNotification = em.createQuery("SELECT n FROM " + NotificationEntity.class.getSimpleName()
					+ " n WHERE n.projectId IN (:projects) AND n.timestamp > :initialdate ORDER BY n.timestamp DESC");
			queryNotification.setParameter("projects", projectStringIds);
			queryNotification.setParameter("initialdate", initialDate);
			final List<NotificationEntity> resultList = queryNotification.getResultList();

			return (List<Notification>) TYPE_CONVERTER.convert(resultList);
		}
		catch (final TypeConverterException e) {
			throw new PersistenceException("It was not possible to convert the NotificationEntity to it's model equivalent.", e);
		}
		catch (final Exception e) {
			throw new PersistenceException("Not able to retrieve notifications for projects: " + projectIds.toString(), e);
		}
		finally {
			em.close();
		}
	}

	private List<String> getStringListFromUUIDList(final List<UUID> projectIds) {
		final List<String> projectStringIds = new ArrayList<String>();
		for (final UUID uuid : projectIds) {
			projectStringIds.add(uuid.toStringRepresentation());
		}
		return projectStringIds;
	}

	@Override
	public Notification persistOrUpdateNotification(final Notification notification) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			em.getTransaction().begin();
			em.merge((NotificationEntity) TYPE_CONVERTER.convert(notification));
			em.getTransaction().commit();
			return notification;
		}
		catch (final Exception e) {
			e.printStackTrace();
			try {
				em.getTransaction().rollback();
			}
			catch (final Exception f) {
				throw new PersistenceException("It was not possible to persist the notification nor to rollback it.", f);
			}
			throw new PersistenceException("It was not possible to persist the notification.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public List<User> retrieveProjectUsers(final ProjectRepresentation projectRepresentation) throws PersistenceException {
		final List<ProjectAuthorization> retrieveAllAuthorizationsForProject = retrieveAllAuthorizationsForProject(projectRepresentation);
		final List<User> projectUsers = new ArrayList<User>();

		for (final ProjectAuthorization projectAuthorization : retrieveAllAuthorizationsForProject) {
			try {
				projectUsers.add(retrieveUserById(projectAuthorization.getUserId()));
			}
			catch (final NoResultFoundException e) {
				e.printStackTrace();
			}
		}

		return projectUsers;
	}
}
