package br.com.oncast.ontrack.server.services.persistence.jpa;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.mocks.actions.ActionMock;
import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;

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
			final ArrayList<ModelAction> actionList = new ArrayList<ModelAction>();
			actionList.add(action);
			persistenceService.persistActions(actionList, calendar.getTime());
			calendar.set(++year, 1, 1);
		}

		final Date dateAfterSomeActionsWerePersisted = calendar.getTime();

		final List<ModelAction> secondWaveOfActions = ActionMock.getActions2();
		for (final ModelAction action : secondWaveOfActions) {
			calendar.set(++year, 1, 1);
			final ArrayList<ModelAction> actionList = new ArrayList<ModelAction>();
			actionList.add(action);
			persistenceService.persistActions(actionList, calendar.getTime());
		}

		final List<ModelAction> actionsReceived = persistenceService.retrieveActionsSince(dateAfterSomeActionsWerePersisted);
		assertEquals(secondWaveOfActions.size(), actionsReceived.size());

		for (int i = 0; i < secondWaveOfActions.size(); i++) {
			assertEquals(secondWaveOfActions.get(i).getReferenceId(), actionsReceived.get(i).getReferenceId());
		}
	}

	@Test
	public void shouldRetrieveSnapshotCorrectly() throws Exception {
		final ProjectSnapshot snapshot1 = persistenceService.retrieveProjectSnapshot();
		final Project project1 = snapshot1.getProject();

		snapshot1.setProject(project1);
		snapshot1.setTimestamp(new Date());
		persistenceService.persistProjectSnapshot(snapshot1);

		final ProjectSnapshot snapshot2 = persistenceService.retrieveProjectSnapshot();
		final Project project2 = snapshot2.getProject();

		DeepEqualityTestUtils.assertObjectEquality(project1, project2);
	}

	@Test
	public void shouldRetrieveSnapshotAfterExecutingActionAndPersistingIt() throws Exception {
		final ProjectSnapshot snapshot1 = persistenceService.retrieveProjectSnapshot();
		final Project project1 = snapshot1.getProject();

		new ScopeInsertChildAction(project1.getProjectScope().getId(), "big son").execute(new ProjectContext(project1));

		snapshot1.setProject(project1);
		snapshot1.setTimestamp(new Date());
		persistenceService.persistProjectSnapshot(snapshot1);

		final ProjectSnapshot snapshot2 = persistenceService.retrieveProjectSnapshot();
		final Project project2 = snapshot2.getProject();

		DeepEqualityTestUtils.assertObjectEquality(project1, project2);
	}
}
