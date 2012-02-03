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

public class KanbanColumnCreateActionTest {

	private ProjectContext context;

	@Before
	public void setUp() {
		context = new ProjectContext(ProjectTestUtils.createPopulatedProject());
	}

	@Test
	public void executionShouldCreateNewKanbanColumnNamedBlabla() throws UnableToCompleteActionException {
		final Release release = context.getProjectRelease().getChild(0);
		final String newColumnDescription = "Blabla";

		ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
		new KanbanColumnCreateAction(release.getId(), newColumnDescription, true).execute(context);
		ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, newColumnDescription,
				ProgressState.DONE.getDescription());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void executionShouldFailWhenTryingToCreateNewKanbanColumnThatAlreadyExists() throws UnableToCompleteActionException {
		final Release release = context.getProjectRelease().getChild(0);
		final String newColumnDescription = "Blabla";

		ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
		try {
			new KanbanColumnCreateAction(release.getId(), newColumnDescription, true).execute(context);
		}
		catch (final Exception e) {
			throw new RuntimeException();
		}
		ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, newColumnDescription,
				ProgressState.DONE.getDescription());
		new KanbanColumnCreateAction(release.getId(), newColumnDescription, true).execute(context);
	}

	@Test
	public void executionShouldCreateTwoNewKanbanColumns() throws UnableToCompleteActionException {
		final Release release = context.getProjectRelease().getChild(0);
		final String newColumnDescription1 = "Blabla";
		final String newColumnDescription2 = "Blibli";

		ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
		new KanbanColumnCreateAction(release.getId(), newColumnDescription1, true).execute(context);
		ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, newColumnDescription1,
				ProgressState.DONE.getDescription());
		new KanbanColumnCreateAction(release.getId(), newColumnDescription2, true).execute(context);
		ActionTestUtils.assertExpectedKanbanColumns(context, release, 4, Progress.DEFAULT_NOT_STARTED_NAME, newColumnDescription1, newColumnDescription2,
				ProgressState.DONE.getDescription());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void executionShouldFailWhenTryingToCreateNewKanbanColumnNamedDone() throws UnableToCompleteActionException {
		final Release release = context.getProjectRelease().getChild(0);
		final String newColumnDescription = ProgressState.DONE.getDescription();

		new KanbanColumnCreateAction(release.getId(), newColumnDescription, true).execute(context);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void executionShouldFailWhenTryingToCreateNewKanbanColumnNamedNotStarted() throws UnableToCompleteActionException {
		final Release release = context.getProjectRelease().getChild(0);
		final String newColumnDescription = Progress.DEFAULT_NOT_STARTED_NAME;

		new KanbanColumnCreateAction(release.getId(), newColumnDescription, true).execute(context);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void executionShouldFailWhenTryingToCreateNewKanbanColumnNamedBlank() throws UnableToCompleteActionException {
		final Release release = context.getProjectRelease().getChild(0);
		final String newColumnDescription = "";

		new KanbanColumnCreateAction(release.getId(), newColumnDescription, true).execute(context);
	}
}
