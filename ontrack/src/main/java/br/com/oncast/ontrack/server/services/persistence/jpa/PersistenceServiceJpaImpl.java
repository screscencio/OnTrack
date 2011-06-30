package br.com.oncast.ontrack.server.services.persistence.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.jpa.entities.ActionContainerEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entities.ActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.BeanMapper;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;

public class PersistenceServiceJpaImpl implements PersistenceService {

	private final static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ontrackPU");

	@Override
	public void persist(final ModelAction modelAction) {
		System.out.println("Persisting entity...");

		final ActionEntity entity = BeanMapper.map(modelAction);
		final ActionContainerEntity container = new ActionContainerEntity();
		container.setAction(entity);

		final EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(container);
		entityManager.getTransaction().commit();

		System.out.println("Entity persisted.");
	}
}