package br.com.oncast.ontrack.server.services.persistence.jpa;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.mocks.actions.ActionMock;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;

public class PersistenceServiceJpaTest {

	private PersistenceServiceJpaImpl persistenceService;

	private EntityManager entityManager;

	@Before
	public void before() {
		entityManager = Persistence.createEntityManagerFactory("ontrackPU").createEntityManager();
		persistenceService = new PersistenceServiceJpaImpl();
	}

	@After
	public void tearDown() {
		entityManager.close();
	}

	@Test
	public void shouldOnlyReturnActionsAfterAGivenDate() throws Exception {
		for (final ModelAction action : ActionMock.getActions()) {
			persistenceService.persistAction(action, new Date());
		}

		Thread.sleep(500);
		final Date dateAfterSomeActionsWerePersisted = new Date();
		Thread.sleep(500);

		final List<ModelAction> secondWaveOfActions = ActionMock.getActions2();
		for (final ModelAction action : secondWaveOfActions) {
			persistenceService.persistAction(action, new Date());
		}

		final List<ModelAction> actionsReceived = persistenceService.retrieveActionsSince(dateAfterSomeActionsWerePersisted);
		assertEquals(secondWaveOfActions.size(), actionsReceived.size());

		for (int i = 0; i < secondWaveOfActions.size(); i++) {
			assertEquals(secondWaveOfActions.get(i).getReferenceId(), actionsReceived.get(i).getReferenceId());
		}
	}
}
