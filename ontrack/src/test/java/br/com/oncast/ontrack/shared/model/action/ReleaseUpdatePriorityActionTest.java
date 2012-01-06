package br.com.oncast.ontrack.shared.model.action;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;

public class ReleaseUpdatePriorityActionTest {

	private ProjectContext context;
	private Scope rootScope;
	private Release rootRelease;

	@Before
	public void setUp() {
		rootScope = ScopeTestUtils.getScope();
		rootRelease = ReleaseTestUtils.getRelease();
		context = ProjectTestUtils.createProjectContext(rootScope, rootRelease);

		assertEquals(3, rootRelease.getChildren().size());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotUpdatePriorityOfARootRelease() throws UnableToCompleteActionException, ReleaseNotFoundException {
		new ReleaseUpdatePriorityAction(rootRelease.getId(), 1).execute(context);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotUpdatePriorityWhenTheTargetPositionDoesNotExists() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release release = rootRelease.getChild(0).getChild(1);
		new ReleaseUpdatePriorityAction(release.getId(), release.getParent().getChildren().size()).execute(context);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotUpdatePriorityWhenTheTargetPositionDoesNotExists2() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release release = rootRelease.getChild(0).getChild(1);
		new ReleaseUpdatePriorityAction(release.getId(), release.getParent().getChildren().size() + 1).execute(context);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotUpdatePriorityWhenTheTargetPositionIsNegative() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release release = rootRelease.getChild(0).getChild(1);
		new ReleaseUpdatePriorityAction(release.getId(), -1).execute(context);
	}

	@Test
	public void shouldIncreaseReleasePriority() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release release = rootRelease.getChild(0).getChild(1);
		new ReleaseUpdatePriorityAction(release.getId(), release.getParent().getChildIndex(release) - 1).execute(context);

		assertEquals(0, rootRelease.getChild(0).getChildIndex(release));
		assertEquals(rootRelease.getChild(0).getChild(0), release);
	}

	@Test
	public void shouldDecreaseReleasePriority() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release release = rootRelease.getChild(0).getChild(1);
		new ReleaseUpdatePriorityAction(release.getId(), release.getParent().getChildIndex(release) + 1).execute(context);

		assertEquals(2, rootRelease.getChild(0).getChildIndex(release));
		assertEquals(rootRelease.getChild(0).getChild(2), release);
	}

	@Test
	public void shouldChangeReleasePriorityToAnyGivenAllowedPosition() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release release = rootRelease.getChild(0);
		new ReleaseUpdatePriorityAction(release.getId(), 2).execute(context);

		assertEquals(2, rootRelease.getChildIndex(release));
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotIncreasePriorityWhenTheReleaseAlreadyHaveTheHighestPossiblePriority() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release release = rootRelease.getChild(0).getChild(0);
		new ReleaseUpdatePriorityAction(release.getId(), release.getParent().getChildIndex(release) - 1).execute(context);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotDecreasePriorityWhenTheReleaseAlreadyHaveTheLowestPossiblePriority() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release release = rootRelease.getChild(0).getChild(2);
		new ReleaseUpdatePriorityAction(release.getId(), release.getParent().getChildIndex(release) + 1).execute(context);
	}

	@Test
	public void shouldNotChangePriorityOfChildrenReleasesWhenChangingAReleasePriority() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release release = rootRelease.getChild(0);
		final Release childRelease0 = release.getChild(0);
		final Release childRelease1 = release.getChild(1);
		final Release childRelease2 = release.getChild(2);

		new ReleaseUpdatePriorityAction(release.getId(), 1).execute(context);

		assertEquals(1, rootRelease.getChildIndex(release));
		assertEquals(rootRelease.getChild(1), release);
		assertEquals(0, release.getChildIndex(childRelease0));
		assertEquals(1, release.getChildIndex(childRelease1));
		assertEquals(2, release.getChildIndex(childRelease2));
	}

	@Test
	public void rollbackShouldChangeReleasePriorityToItsOldPriority() throws UnableToCompleteActionException {
		final Release release = rootRelease.getChild(0);
		final ModelAction rollbackAction = new ReleaseUpdatePriorityAction(release.getId(), 1).execute(context);

		assertEquals(1, rootRelease.getChildIndex(release));
		assertEquals(rootRelease.getChild(1), release);

		rollbackAction.execute(context);

		assertEquals(0, rootRelease.getChildIndex(release));
		assertEquals(rootRelease.getChild(0), release);
	}

	@Test
	public void rollbackShouldChangeReleasePriorityToItsOldPriority2() throws UnableToCompleteActionException {
		final Release release = rootRelease.getChild(0);
		final ModelAction rollbackAction = new ReleaseUpdatePriorityAction(release.getId(), 2).execute(context);

		assertEquals(2, rootRelease.getChildIndex(release));
		assertEquals(rootRelease.getChild(2), release);

		rollbackAction.execute(context);

		assertEquals(0, rootRelease.getChildIndex(release));
		assertEquals(rootRelease.getChild(0), release);
	}

	@Test
	public void shouldHandlePriorityUpdateCorrectlyAfterMultipleUndosAndRedos() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release release = rootRelease.getChild(0);

		final ActionExecutionManager actionExecutionManager = new ActionExecutionManager(Mockito.mock(ActionExecutionListener.class));
		actionExecutionManager.doUserAction(new ReleaseUpdatePriorityAction(release.getId(), 1), context);

		assertEquals(1, rootRelease.getChildIndex(release));
		assertEquals(rootRelease.getChild(1), release);

		for (int i = 0; i < 20; i++) {
			actionExecutionManager.undoUserAction(context);

			assertEquals(0, rootRelease.getChildIndex(release));
			assertEquals(rootRelease.getChild(0), release);

			actionExecutionManager.redoUserAction(context);

			assertEquals(1, rootRelease.getChildIndex(release));
			assertEquals(rootRelease.getChild(1), release);
		}
	}
}
