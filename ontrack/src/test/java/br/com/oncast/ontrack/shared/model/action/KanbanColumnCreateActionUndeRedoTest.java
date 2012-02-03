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

public class KanbanColumnCreateActionUndeRedoTest {
	private ProjectContext context;

	@Before
	public void setUp() {
		context = new ProjectContext(ProjectTestUtils.createPopulatedProject());
	}

	@Test
	public void executionShouldLockTheUnlockedKanban() throws UnableToCompleteActionException {
		final Release release = context.getProjectRelease().getChild(0);
		final String newColumnDescription = "Blabla";

		ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
		Assert.assertFalse("The kanban should be unlocked.", context.getKanban(release).isLocked());

		new KanbanColumnCreateAction(release.getId(), newColumnDescription, true).execute(context);

		ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, newColumnDescription,
				ProgressState.DONE.getDescription());
		Assert.assertTrue("The kanban should be locked.", context.getKanban(release).isLocked());
	}

	@Test
	public void executionShouldNotLockTheUnlockedKanbanWhenParametersSaySo() throws UnableToCompleteActionException {
		final Release release = context.getProjectRelease().getChild(0);
		final String newColumnDescription = "Blabla";

		ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
		Assert.assertFalse("The kanban should be unlocked.", context.getKanban(release).isLocked());

		new KanbanColumnCreateAction(release.getId(), newColumnDescription, false).execute(context);

		ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, newColumnDescription,
				ProgressState.DONE.getDescription());
		Assert.assertFalse("The kanban should be unlocked.", context.getKanban(release).isLocked());
	}

	@Test
	public void kanbanShouldRemainLockedWhenExecutionIsNotMeantToLockIt() throws UnableToCompleteActionException {
		final Release release = context.getProjectRelease().getChild(0);
		final String newColumnDescription = "Blabla";
		context.getKanban(release).setLocked(true);

		ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
		Assert.assertTrue("The kanban should be locked.", context.getKanban(release).isLocked());

		new KanbanColumnCreateAction(release.getId(), newColumnDescription, false).execute(context);

		ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, newColumnDescription,
				ProgressState.DONE.getDescription());
		Assert.assertTrue("The kanban should be locked.", context.getKanban(release).isLocked());
	}

	@Test
	public void doUndoAndRedoShouldReturnToTheState() throws UnableToCompleteActionException {
		final Release release = context.getProjectRelease().getChild(0);
		final String newColumnDescription = "Blabla";

		ModelAction action = new KanbanColumnCreateAction(release.getId(), newColumnDescription, true);
		ModelAction rollbackAction;

		for (int i = 0; i < 10; i++) {
			rollbackAction = action.execute(context);
			ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, newColumnDescription,
					ProgressState.DONE.getDescription());
			action = rollbackAction.execute(context);
			ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
		}
	}

	@Test
	public void undoShouldNotUnlockThePreviouslyUnlockedKanban() throws UnableToCompleteActionException {
		final Release release = context.getProjectRelease().getChild(0);
		final String newColumnDescription = "Blabla";
		Assert.assertFalse("The kanban should be unlocked.", context.getKanban(release).isLocked());

		ModelAction action = new KanbanColumnCreateAction(release.getId(), newColumnDescription, true);
		ModelAction rollbackAction;

		for (int i = 0; i < 10; i++) {
			rollbackAction = action.execute(context);
			Assert.assertTrue("The kanban should be locked.", context.getKanban(release).isLocked());

			action = rollbackAction.execute(context);
			ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
			Assert.assertTrue("The kanban should be locked.", context.getKanban(release).isLocked());
		}
	}
}
