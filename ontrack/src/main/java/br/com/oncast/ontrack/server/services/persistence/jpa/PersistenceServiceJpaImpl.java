package br.com.oncast.ontrack.server.services.persistence.jpa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.services.persistence.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.ActionContainerEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.util.converter.GeneralTypeConverter;
import br.com.oncast.ontrack.server.util.converter.exceptions.BeanConverterException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.Project;

// TODO Extract EntityManager logic to a "EntityManagerManager" (Using a better name).
public class PersistenceServiceJpaImpl implements PersistenceService {

	private final static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ontrackPU");

	// FIXME Verificar exceções no JPA
	@Override
	public void persist(final ModelAction modelAction, final Date timestamp) throws PersistenceException {
		final ModelActionEntity entity = convertActionToEntity(modelAction);
		final ActionContainerEntity container = new ActionContainerEntity(entity, timestamp);

		final EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.persist(container);
		em.getTransaction().commit();
	}

	// TODO Change to an implementation that use a database
	@Override
	public ProjectSnapshot retrieveProjectSnapshot() {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(2011, 1, 1);

		return new ProjectSnapshot(new Project(), calendar.getTime());
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ModelAction> retrieveActionsSince(final Date timestamp) throws PersistenceException {
		final EntityManager em = entityManagerFactory.createEntityManager();
		final Query query = em
				.createQuery("select action from ActionContainerEntity as action where action.timestamp > ?timestamp order by action.timestamp asc");
		query.setParameter("timestamp", timestamp, TemporalType.TIMESTAMP);
		final List<ActionContainerEntity> actions = query.getResultList();

		final List<ModelAction> modelActionList = new ArrayList<ModelAction>();
		for (final ActionContainerEntity action : actions) {
			try {
				modelActionList.add((ModelAction) new GeneralTypeConverter().convert(action.getActionEntity()));
			}
			catch (final BeanConverterException e) {
				throw new PersistenceException("There was not possible to retrieve actions.", e);
			}
		}

		return modelActionList;
	}

	private ModelActionEntity convertActionToEntity(final ModelAction modelAction) throws PersistenceException {
		ModelActionEntity entity;
		try {
			entity = (ModelActionEntity) new GeneralTypeConverter().convert(modelAction);
		}
		catch (final BeanConverterException e) {
			throw new PersistenceException("It was not possible to convert the action to its entity", e);
		}
		return entity;
	}

}