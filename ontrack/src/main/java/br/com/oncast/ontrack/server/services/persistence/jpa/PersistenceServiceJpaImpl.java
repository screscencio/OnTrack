package br.com.oncast.ontrack.server.services.persistence.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import br.com.oncast.ontrack.server.services.persistence.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.jpa.beanConverter.BeanConverter;
import br.com.oncast.ontrack.server.services.persistence.jpa.beanConverter.exceptions.BeanConverterException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.ActionContainerEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
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

	@Override
	public Project loadProjectSnapshot() {
		final EntityManager em = entityManagerFactory.createEntityManager();
		final Query query = em.createQuery("select action from " + ActionContainerEntity.class.getSimpleName() + " as action");
		final List<ActionContainerEntity> actions = query.getResultList();

		final List<ModelAction> modelActionList = new ArrayList<ModelAction>();
		for (final ActionContainerEntity action : actions) {
			ModelAction modelAction;
			try {
				modelAction = (ModelAction) new BeanConverter().convert(action.getActionEntity());
				modelActionList.add(modelAction);
			}
			catch (final BeanConverterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	private ModelActionEntity convertActionToEntity(final ModelAction modelAction) throws PersistenceException {
		ModelActionEntity entity;
		try {
			entity = (ModelActionEntity) new BeanConverter().convert(modelAction);
		}
		catch (final BeanConverterException e) {
			throw new PersistenceException("It was not possible to convert the action to its entity", e);
		}
		return entity;
	}
}