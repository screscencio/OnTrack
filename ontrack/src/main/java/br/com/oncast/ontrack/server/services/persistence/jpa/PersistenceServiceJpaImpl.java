package br.com.oncast.ontrack.server.services.persistence.jpa;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.UserActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.GeneralTypeConverter;
import br.com.oncast.ontrack.server.utils.typeConverter.exceptions.TypeConverterException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

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

	// FIXME Make business create the blank project if NoResultException is thrown (create a new exception type).
	@Override
	public ProjectSnapshot retrieveProjectSnapshot() throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select snapshot from " + ProjectSnapshot.class.getSimpleName() + " as snapshot");
			return (ProjectSnapshot) query.getSingleResult();
		}
		catch (final NoResultException e) {
			return createBlankProject();
		}
		catch (final Exception e) {
			throw new PersistenceException("It was not possible to retrieve the project snapshot.", e);
		}
		finally {
			em.close();
		}
	}

	private ProjectSnapshot createBlankProject() throws PersistenceException {
		final Scope projectScope = new Scope("Project", new UUID("0"));
		final Release projectRelease = new Release("proj", new UUID("release0"));

		try {
			return new ProjectSnapshot(new Project(projectScope, projectRelease), new Date(0));
		}
		catch (final IOException e) {
			throw new PersistenceException("It was not possible to create a blank project.", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ModelAction> retrieveActionsSince(final Date timestamp) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			final Query query = em.createQuery("select action from " + UserActionEntity.class.getSimpleName()
					+ " as action where action.timestamp > :timestamp order by action.id asc");

			query.setParameter("timestamp", timestamp, TemporalType.TIMESTAMP);
			final List<UserActionEntity> actions = query.getResultList();

			return (List<ModelAction>) TYPE_CONVERTER.convert(actions);
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