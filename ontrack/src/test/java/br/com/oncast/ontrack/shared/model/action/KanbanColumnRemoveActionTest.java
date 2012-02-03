package br.com.oncast.ontrack.shared.model.action;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

public class KanbanColumnRemoveActionTest {

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
	public void executionShouldRemoveKanbanColumnNamedBlabla() throws UnableToCompleteActionException {
		ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, columnDescription,
				ProgressState.DONE.getDescription());
		new KanbanColumnRemoveAction(release.getId(), columnDescription, true).execute(context);
		ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void executionShouldFailWhenTryingToRemoveKanbanColumnThatNotExists() throws UnableToCompleteActionException {
		ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, columnDescription,
				ProgressState.DONE.getDescription());

		try {
			new KanbanColumnRemoveAction(release.getId(), columnDescription).execute(context);
		}
		catch (final Exception e) {
			throw new RuntimeException();
		}

		ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());

		new KanbanColumnRemoveAction(release.getId(), columnDescription).execute(context);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void executionShouldFailWhenTryingToRemoveKanbanColumnNamedDone() throws UnableToCompleteActionException {
		final String columnDescription = ProgressState.DONE.getDescription();

		new KanbanColumnRemoveAction(release.getId(), columnDescription, true).execute(context);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void executionShouldFailWhenTryingToRemoveKanbanColumnNamedNotStarted() throws UnableToCompleteActionException {
		final String columnDescription = Progress.DEFAULT_NOT_STARTED_NAME;

		new KanbanColumnRemoveAction(release.getId(), columnDescription, true).execute(context);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void executionShouldFailWhenTryingToRemoveKanbanColumnThatDoesNotExist() throws UnableToCompleteActionException {
		final String columnDescription = "";

		new KanbanColumnRemoveAction(release.getId(), columnDescription, true).execute(context);
	}
}
