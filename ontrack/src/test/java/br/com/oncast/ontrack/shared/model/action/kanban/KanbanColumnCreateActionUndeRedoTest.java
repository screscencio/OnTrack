package br.com.oncast.ontrack.shared.model.action.kanban;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnCreateAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;

public class KanbanColumnCreateActionUndeRedoTest {
	private ProjectContext context;
	private Release release;
	private String columnDescription;

	@Before
	public void setUp() {
		context = new ProjectContext(ProjectTestUtils.createPopulatedProject());
		release = context.getProjectRelease().getChild(0);
		columnDescription = "Blabla";
	}

	@Test
	public void executionShouldLockTheUnlockedKanban() throws UnableToCompleteActionException {
		ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
		Assert.assertFalse("The kanban should be unlocked.", context.getKanban(release).isLocked());

		new KanbanColumnCreateAction(release.getId(), columnDescription, true).execute(context, Mockito.mock(ActionContext.class));

		ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, columnDescription,
				ProgressState.DONE.getDescription());
		Assert.assertTrue("The kanban should be locked.", context.getKanban(release).isLocked());
	}

	@Test
	public void executionShouldNotLockTheUnlockedKanbanWhenParametersSaySo() throws UnableToCompleteActionException {
		ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
		Assert.assertFalse("The kanban should be unlocked.", context.getKanban(release).isLocked());

		new KanbanColumnCreateAction(release.getId(), columnDescription, false).execute(context, Mockito.mock(ActionContext.class));

		ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, columnDescription,
				ProgressState.DONE.getDescription());
		Assert.assertFalse("The kanban should be unlocked.", context.getKanban(release).isLocked());
	}

	@Test
	public void kanbanShouldRemainLockedWhenExecutionIsNotMeantToLockIt() throws UnableToCompleteActionException {
		context.getKanban(release).setLocked(true);

		ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
		Assert.assertTrue("The kanban should be locked.", context.getKanban(release).isLocked());

		new KanbanColumnCreateAction(release.getId(), columnDescription, false).execute(context, Mockito.mock(ActionContext.class));

		ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, columnDescription,
				ProgressState.DONE.getDescription());
		Assert.assertTrue("The kanban should be locked.", context.getKanban(release).isLocked());
	}

	@Test
	public void doUndoAndRedoShouldReturnToTheState() throws UnableToCompleteActionException {
		ModelAction action = new KanbanColumnCreateAction(release.getId(), columnDescription, true);
		ModelAction rollbackAction;

		for (int i = 0; i < 10; i++) {
			rollbackAction = action.execute(context, Mockito.mock(ActionContext.class));
			ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, columnDescription,
					ProgressState.DONE.getDescription());
			action = rollbackAction.execute(context, Mockito.mock(ActionContext.class));
			ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
		}
	}

	@Test
	public void undoShouldNotUnlockThePreviouslyUnlockedKanban() throws UnableToCompleteActionException {
		Assert.assertFalse("The kanban should be unlocked.", context.getKanban(release).isLocked());

		ModelAction action = new KanbanColumnCreateAction(release.getId(), columnDescription, true);
		ModelAction rollbackAction;

		for (int i = 0; i < 10; i++) {
			rollbackAction = action.execute(context, Mockito.mock(ActionContext.class));
			Assert.assertTrue("The kanban should be locked.", context.getKanban(release).isLocked());

			action = rollbackAction.execute(context, Mockito.mock(ActionContext.class));
			ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
			Assert.assertTrue("The kanban should be locked.", context.getKanban(release).isLocked());
		}
	}

	@Test
	public void doUndoAndRedoShouldRecreateKanbanColumnInTheSamePosition() throws UnableToCompleteActionException {
		final String columnBefore = columnDescription + "Before";
		final String columnAfter = columnDescription + "After";

		new KanbanColumnCreateAction(release.getId(), columnBefore, true).execute(context, Mockito.mock(ActionContext.class));
		new KanbanColumnCreateAction(release.getId(), columnAfter, true).execute(context, Mockito.mock(ActionContext.class));

		ModelAction action = new KanbanColumnCreateAction(release.getId(), columnDescription, true, 1);
		ModelAction rollbackAction;

		for (int i = 0; i < 10; i++) {
			ActionTestUtils.assertExpectedKanbanColumns(context, release, 4, Progress.DEFAULT_NOT_STARTED_NAME, columnBefore, columnAfter,
					ProgressState.DONE.getDescription());

			rollbackAction = action.execute(context, Mockito.mock(ActionContext.class));

			ActionTestUtils.assertExpectedKanbanColumns(context, release, 5, Progress.DEFAULT_NOT_STARTED_NAME, columnBefore, columnDescription,
					columnAfter, ProgressState.DONE.getDescription());

			action = rollbackAction.execute(context, Mockito.mock(ActionContext.class));
		}
	}
}
