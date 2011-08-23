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

import com.ibm.icu.util.Calendar;

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
		final Calendar calendar = Calendar.getInstance();
		int year = 1971;
		calendar.set(year, 1, 1);

		for (final ModelAction action : ActionMock.getActions()) {
			persistenceService.persistAction(action, calendar.getTime());
			calendar.set(++year, 1, 1);
		}

		final Date dateAfterSomeActionsWerePersisted = calendar.getTime();

		final List<ModelAction> secondWaveOfActions = ActionMock.getActions2();
		for (final ModelAction action : secondWaveOfActions) {
			calendar.set(++year, 1, 1);
			persistenceService.persistAction(action, calendar.getTime());
		}

		final List<ModelAction> actionsReceived = persistenceService.retrieveActionsSince(dateAfterSomeActionsWerePersisted);
		assertEquals(secondWaveOfActions.size(), actionsReceived.size());

		for (int i = 0; i < secondWaveOfActions.size(); i++) {
			assertEquals(secondWaveOfActions.get(i).getReferenceId(), actionsReceived.get(i).getReferenceId());
		}
	}
}
