package br.com.oncast.ontrack.server.services.persistence.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

import br.com.oncast.ontrack.server.business.UserAction;
import br.com.oncast.ontrack.server.model.Password;
import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.UserActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.user.PasswordEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.user.UserEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.GeneralTypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.user.User;

// TODO ++Extract EntityManager logic to a "EntityManagerManager" (Using a better name).
// TODO Analise using CriteriaApi instead of HQL.
// TODO Implement better exception handling for JPA exceptions
public class PersistenceServiceJpaImpl implements PersistenceService {

	private final static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ontrackPU");
	private final static GeneralTypeConverter TYPE_CONVERTER = new GeneralTypeConverter();

	@Override
	public void persistActions(final List<ModelAction> actionList, final Date timestamp) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			em.getTransaction().begin();
			for (final ModelAction modelAction : actionList) {
				final ModelActionEntity entity = convertActionToEntity(modelAction);
				final UserActionEntity container = new UserActionEntity(entity, timestamp);
				em.persist(container);
			}
			em.getTransaction().commit();
		}
		catch (final Exception e) {
			try {
				em.getTransaction().rollback();
			}
			catch (final Exception f) {
				throw new PersistenceException("It was not possible to neither persist a group of actions and to rollback it.", f);
			}
			throw new PersistenceException("It was not possible to persist a group of actions.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public ProjectSnapshot retrieveProjectSnapshot() throws PersistenceException, NoResultFoundException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select snapshot from " + ProjectSnapshot.class.getSimpleName() + " as snapshot");
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
	@SuppressWarnings("unchecked")
	public List<UserAction> retrieveActionsSince(final long actionId) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select action from " + UserActionEntity.class.getSimpleName()
					+ " as action where action.id > :lastActionId order by action.id asc");

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
				throw new PersistenceException("It was not possible to persist the project snapshot and to rollback it.", f);
			}
			throw new PersistenceException("It was not possible to persist the project snapshot.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public User findUserByEmail(final String email) throws NoResultFoundException, PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select user from " + UserEntity.class.getSimpleName() + " as user where user.email = :email");
			query.setParameter("email", email);

			return convertEntityToUser((UserEntity) query.getSingleResult());
		}
		catch (final NoResultException e) {
			throw new NoResultFoundException("No user found with email: " + email, e);
		}
		catch (final Exception e) {
			throw new PersistenceException("It was not possible to retrieve the user.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public void persistOrUpdateUser(final User user) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {

			final UserEntity userEntity = (UserEntity) TYPE_CONVERTER.convert(user);
			em.getTransaction().begin();
			em.merge(userEntity);
			em.getTransaction().commit();
		}
		catch (final Exception e) {
			try {
				em.getTransaction().rollback();
			}
			catch (final Exception f) {
				throw new PersistenceException("It was not possible to persist the user and to rollback it.", f);
			}
			throw new PersistenceException("It was not possible to persist the user.", e);
		}
		finally {
			em.close();
		}
	}

	@Override
	public void persistPassword(final Password passwordForUser) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {

			final PasswordEntity passwordEntity = (PasswordEntity) TYPE_CONVERTER.convert(passwordForUser);
			em.getTransaction().begin();
			em.merge(passwordEntity);
			em.getTransaction().commit();
		}
		catch (final Exception f) {
			throw new PersistenceException("It was not possible to persist the user and to rollback it.", f);
		}
		finally {
			em.close();
		}
	}

	@Override
	public Password findPasswordForUserId(final long userId) throws NoResultFoundException, PersistenceException {
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

	private User convertEntityToUser(final UserEntity userEntity) throws PersistenceException {
		final User user;
		try {
			user = (User) TYPE_CONVERTER.convert(userEntity);
		}
		catch (final TypeConverterException e) {
			throw new PersistenceException("It was not possible to convert the user to its entity", e);
		}
		return user;
	}
}
