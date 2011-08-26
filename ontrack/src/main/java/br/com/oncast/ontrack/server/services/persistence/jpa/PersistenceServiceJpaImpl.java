package br.com.oncast.ontrack.server.services.persistence.jpa;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
	public void persistAction(final ModelAction modelAction, final Date timestamp) throws PersistenceException {
		final ModelActionEntity entity = convertActionToEntity(modelAction);
		final UserActionEntity container = new UserActionEntity(entity, timestamp);

		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(container);
			em.getTransaction().commit();
		}
		catch (final Exception e) {
			throw new PersistenceException("It was not possible to persist an action.", e);
		}
		finally {
			em.close();
		}
	}

	// TODO Implement a query to obtain a real project snapshot instead of this mocked one.
	// TODO Change the business logic test, because it assume the inclusion of the 'Example Scope' bellow.
	@Override
	public ProjectSnapshot retrieveProjectSnapshot() {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(2011, 1, 1);

		final Scope projectScope = new Scope("Project", new UUID("0"));
		return new ProjectSnapshot(new Project(projectScope, new Release("proj")), calendar.getTime());
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ModelAction> retrieveActionsSince(final Date timestamp) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		try {
			// TODO +++ Order by ID, not timestamp.
			final Query query = em.createQuery("select action from " + UserActionEntity.class.getSimpleName()
					+ " as action where action.timestamp > :timestamp order by action.timestamp asc");

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