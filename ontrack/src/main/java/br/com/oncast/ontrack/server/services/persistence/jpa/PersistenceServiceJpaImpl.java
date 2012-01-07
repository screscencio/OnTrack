package br.com.oncast.ontrack.server.services.persistence.jpa;

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
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.UserActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.user.PasswordEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.GeneralTypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;

// TODO ++Extract EntityManager logic to a "EntityManagerManager" (Using a better name).
// TODO Analise using CriteriaApi instead of HQL.
// TODO Implement better exception handling for JPA exceptions
// TODO Separate authentication/authorization persistence methods from business related methods.
// TODO Extract rollback treatment to a method in all the persist methods.
public class PersistenceServiceJpaImpl implements PersistenceService {

	private final static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ontrackPU");
	private final static GeneralTypeConverter TYPE_CONVERTER = new GeneralTypeConverter();

	@Override
	public void persistActions(final long projectId, final List<ModelAction> actionList, final Date timestamp) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			em.getTransaction().begin();
			final ProjectRepresentation projectRepresentation = retrieveProjectRepresentation(projectId);
			for (final ModelAction modelAction : actionList) {
				final ModelActionEntity entity = convertActionToEntity(modelAction);
				final UserActionEntity container = new UserActionEntity(entity, projectRepresentation, timestamp);
				em.persist(container);
			}
			em.getTransaction().commit();
		}
		catch (final Exception e) {
			try {
				em.getTransaction().rollback();
			}
			catch (final Exception f) {
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
	public List<UserAction> retrieveActionsSince(final long projectId, final long actionId) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select action from " + UserActionEntity.class.getSimpleName()
					+ " as action where action.projectRepresentation.id = :projectId and action.id > :lastActionId order by action.id asc");

			query.setParameter("projectId", projectId);
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
				throw new PersistenceException("It was not possible to persist the project snapshot nor to rollback it.", f);
			}
			throw new PersistenceException("It was not possible to persist the project snapshot.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public ProjectSnapshot retrieveProjectSnapshot(final long projectId) throws PersistenceException, NoResultFoundException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select snapshot from " + ProjectSnapshot.class.getSimpleName()
					+ " as snapshot where snapshot.projectRepresentation.id = :projectId");
			query.setParameter("projectId", projectId);
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

			em.getTransaction().begin();
			final User mergedUser = em.merge(user);
			em.getTransaction().commit();
			// FIXME Make this method void and change the incoming object with id.
			return mergedUser;
		}
		catch (final Exception e) {
			try {
				em.getTransaction().rollback();
			}
			catch (final Exception f) {
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
			final Query query = em.createQuery("select user from " + User.class.getSimpleName() + " as user where user.email = :email");
			query.setParameter("email", email);

			return (User) query.getSingleResult();
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
	public List<User> retrieveAllUsers() throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select user from " + User.class.getSimpleName() + " as user");

			return query.getResultList();
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
			final PasswordEntity mergedEntity = em.merge(passwordEntity);
			em.getTransaction().commit();
			passwordForUser.setId(mergedEntity.getId());
		}
		catch (final Exception e) {
			try {
				em.getTransaction().rollback();
			}
			catch (final Exception f) {
				throw new PersistenceException("It was not possible to persist the password nor to rollback it.", f);
			}
			throw new PersistenceException("It was not possible to persist the password.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public Password retrievePasswordForUser(final long userId) throws NoResultFoundException, PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select password from " + PasswordEntity.class.getSimpleName() + " as password where password.userId = :userId");
			query.setParameter("userId", userId);

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
			final ProjectRepresentation mergedProjectRepresentation = em.merge(projectRepresentation);
			em.getTransaction().commit();
			projectRepresentation.setId(mergedProjectRepresentation.getId());
			// FIXME Make this method void, because it is already changing the incoming object with generated id.
			return mergedProjectRepresentation;
		}
		catch (final Exception e) {
			try {
				em.getTransaction().rollback();
			}
			catch (final Exception f) {
				throw new PersistenceException("It was not possible to persist the project representation nor to rollback it.", f);
			}
			throw new PersistenceException("It was not possible to persist the project representation.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public ProjectRepresentation retrieveProjectRepresentation(final long projectId) throws PersistenceException, NoResultFoundException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select projectRepresentation from " + ProjectRepresentation.class.getSimpleName()
					+ " as projectRepresentation where projectRepresentation.id = :projectId");

			query.setParameter("projectId", projectId);
			return (ProjectRepresentation) query.getSingleResult();
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

			return query.getResultList();
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
	public void authorize(final String userEmail, final long projectId) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			em.getTransaction().begin();
			final ProjectAuthorization authorization = new ProjectAuthorization(retrieveUserByEmail(userEmail), em.find(
					ProjectRepresentation.class, projectId));
			em.persist(authorization);
			em.getTransaction().commit();
		}
		catch (final Exception e) {
			try {
				em.getTransaction().rollback();
			}
			catch (final Exception f) {
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
	public List<ProjectAuthorization> retrieveProjectAuthorizations(final long userId) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select authorization from " + ProjectAuthorization.class.getSimpleName()
					+ " as authorization where authorization.user.id = :userId");
			query.setParameter("userId", userId);
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
			throw new PersistenceException("It was not possible to retrieve the project representations", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public ProjectAuthorization retrieveProjectAuthorization(final long userId, final long projectId) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select authorization from " + ProjectAuthorization.class.getSimpleName()
					+ " as authorization where authorization.user.id = :userId and authorization.project.id = :projectId");
			query.setParameter("userId", userId);
			query.setParameter("projectId", projectId);
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
}
