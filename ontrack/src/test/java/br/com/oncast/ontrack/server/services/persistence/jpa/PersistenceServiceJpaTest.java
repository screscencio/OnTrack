package br.com.oncast.ontrack.server.services.persistence.jpa;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.mocks.actions.ActionMock;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;

public class PersistenceServiceJpaTest {

	private PersistenceServiceJpaImpl persistenceService;

	@Before
	public void before() {
		persistenceService = new PersistenceServiceJpaImpl();
	}

	@Test
	public void shouldOnlyReturnActionsAfterAGivenDate() throws Exception {
		for (final ModelAction action : ActionMock.getActions()) {
			persistenceService.persistAction(action, new Date());
		}

		Thread.sleep(1000);
		final Date dateAfterSomeActionsWerePersisted = new Date();
		Thread.sleep(1000);

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