package br.com.oncast.ontrack.server.services.persistence.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.ActionContainerEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.BeanMapper;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.Project;

public class PersistenceServiceJpaImpl implements PersistenceService {

	private final static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ontrackPU");

	@Override
	public void persist(final ModelAction modelAction) {
		System.out.println("Persisting entity...");

		final ModelActionEntity entity = (ModelActionEntity) BeanMapper.map(modelAction);
		final ActionContainerEntity container = new ActionContainerEntity();
		container.setAction(entity);

		final EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.persist(container);
		em.getTransaction().commit();

		System.out.println("Entity persisted.");
	}

	@Override
	public Project load() {
		final EntityManager em = entityManagerFactory.createEntityManager();
		final Query query = em.createQuery("select action from " + ActionContainerEntity.class.getSimpleName() + " as action");
		final List<ActionContainerEntity> actions = query.getResultList();

		final List<ModelAction> modelActionList = new ArrayList<ModelAction>();
		for (final ActionContainerEntity action : actions) {
			final ModelAction modelAction = (ModelAction) BeanMapper.map(action.getAction());
			modelActionList.add(modelAction);
		}

		return null;
	}
}