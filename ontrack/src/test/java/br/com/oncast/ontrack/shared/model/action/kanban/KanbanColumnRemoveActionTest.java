package br.com.oncast.ontrack.shared.model.action.kanban;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban.KanbanColumnRemoveActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnCreateAction;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;

public class KanbanColumnRemoveActionTest extends ModelActionTest {

	private ProjectContext context;
	private Release release;
	private String columnDescription;

	@Before
	public void setUp() throws UnableToCompleteActionException {
		context = new ProjectContext(ProjectTestUtils.createPopulatedProject());
		release = context.getProjectRelease().getChild(0);
		columnDescription = "Blabla";
		new KanbanColumnCreateAction(release.getId(), columnDescription, false).execute(context, Mockito.mock(ActionContext.class));
	}

	@Test
	public void shouldSetProgressOfItsContentsToPreviousKanbanColumn() throws Exception {
		release.getScopeList().get(0).add(ScopeTestUtils.createScope());

		final List<Scope> tasks = release.getTasks();
		for (final Scope t : tasks) {
			ScopeTestUtils.setProgress(t, columnDescription);
		}
		new KanbanColumnRemoveAction(release.getId(), columnDescription, true).execute(context, actionContext);

		for (final Scope t : tasks) {
			assertEquals(ProgressState.NOT_STARTED.getDescription(), t.getProgress().getDescription());
		}
	}

	@Test
	public void executionShouldRemoveKanbanColumnNamedBlabla() throws UnableToCompleteActionException {
		ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, columnDescription,
				ProgressState.DONE.getDescription());
		new KanbanColumnRemoveAction(release.getId(), columnDescription, true).execute(context, Mockito.mock(ActionContext.class));
		ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
	}

	@Test
	public void nothingShouldHappenWhenTryingToRemoveKanbanColumnThatNotExists() throws UnableToCompleteActionException {
		ActionTestUtils.assertExpectedKanbanColumns(context, release, 3, Progress.DEFAULT_NOT_STARTED_NAME, columnDescription,
				ProgressState.DONE.getDescription());

		new KanbanColumnRemoveAction(release.getId(), columnDescription).execute(context, Mockito.mock(ActionContext.class));

		ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());

		new KanbanColumnRemoveAction(release.getId(), columnDescription).execute(context, Mockito.mock(ActionContext.class));
		ActionTestUtils.assertExpectedKanbanColumns(context, release, 2, Progress.DEFAULT_NOT_STARTED_NAME, ProgressState.DONE.getDescription());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void executionShouldFailWhenTryingToRemoveKanbanColumnNamedDone() throws UnableToCompleteActionException {
		final String columnDescription = ProgressState.DONE.getDescription();

		new KanbanColumnRemoveAction(release.getId(), columnDescription, true).execute(context, Mockito.mock(ActionContext.class));
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void executionShouldFailWhenTryingToRemoveKanbanColumnNamedNotStarted() throws UnableToCompleteActionException {
		final String columnDescription = Progress.DEFAULT_NOT_STARTED_NAME;

		new KanbanColumnRemoveAction(release.getId(), columnDescription, true).execute(context, Mockito.mock(ActionContext.class));
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void executionShouldFailWhenTryingToRemoveKanbanColumnThatDoesNotExist() throws UnableToCompleteActionException {
		final String columnDescription = "";

		new KanbanColumnRemoveAction(release.getId(), columnDescription, true).execute(context, Mockito.mock(ActionContext.class));
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return KanbanColumnRemoveActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return KanbanColumnRemoveAction.class;
	}

	@Override
	protected ModelAction getNewInstance() {
		return new KanbanColumnRemoveAction(new UUID(), columnDescription);
	}
}
