package br.com.oncast.ontrack.shared.model.action;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

public class KanbanColumnRemoveActionUndeRedoTest {

	private ProjectContext context;
	private Release release;
	private String columnDescription;

	@Before
	public void setUp() throws UnableToCompleteActionException {
		context = new ProjectContext(ProjectTestUtils.createPopulatedProject());
		release = context.getProjectRelease().getChild(0);
		columnDescription = "Blabla";
		new KanbanColumnCreateAction(release.getId(), columnDescription, false).execute(context);
	}

	@Test
	public void executionShouldLockTheUnlockedKanban() throws UnableToCompleteActionException {
		Assert.assertFalse("The kanban should be unlocked.", context.getKanban(release).isLocked());
		ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, columnDescription,
				ProgressState.DONE.getDescription());

		new KanbanColumnRemoveAction(release.getId(), columnDescription, true).execute(context);

		ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
		Assert.assertTrue("The kanban should be locked.", context.getKanban(release).isLocked());
	}

	@Test
	public void executionShouldNotLockTheUnlockedKanbanWhenParametersSaySo() throws UnableToCompleteActionException {
		Assert.assertFalse("The kanban should be unlocked.", context.getKanban(release).isLocked());
		ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, columnDescription,
				ProgressState.DONE.getDescription());

		new KanbanColumnRemoveAction(release.getId(), columnDescription, false).execute(context);

		Assert.assertFalse("The kanban should be unlocked.", context.getKanban(release).isLocked());
		ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
	}

	@Test
	public void kanbanShouldRemainLockedWhenExecutionIsNotMeantToLockIt() throws UnableToCompleteActionException {
		context.getKanban(release).setLocked(true);

		Assert.assertTrue("The kanban should be locked.", context.getKanban(release).isLocked());
		ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, columnDescription,
				ProgressState.DONE.getDescription());

		new KanbanColumnRemoveAction(release.getId(), columnDescription, false).execute(context);

		Assert.assertTrue("The kanban should be locked.", context.getKanban(release).isLocked());
		ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
	}

	@Test
	public void doUndoAndRedoShouldReturnToTheState() throws UnableToCompleteActionException {
		ModelAction action = new KanbanColumnRemoveAction(release.getId(), columnDescription, true);
		ModelAction rollbackAction;

		for (int i = 0; i < 10; i++) {
			rollbackAction = action.execute(context);
			ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
			action = rollbackAction.execute(context);
			ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, columnDescription,
					ProgressState.DONE.getDescription());
		}
	}

	@Test
	public void undoShouldNotUnlockThePreviouslyUnlockedKanban() throws UnableToCompleteActionException {
		Assert.assertFalse("The kanban should be unlocked.", context.getKanban(release).isLocked());

		ModelAction action = new KanbanColumnRemoveAction(release.getId(), columnDescription, true);
		ModelAction rollbackAction;

		for (int i = 0; i < 10; i++) {
			rollbackAction = action.execute(context);
			Assert.assertTrue("The kanban should be locked.", context.getKanban(release).isLocked());

			action = rollbackAction.execute(context);
			Assert.assertTrue("The kanban should be locked.", context.getKanban(release).isLocked());
		}
	}
}
