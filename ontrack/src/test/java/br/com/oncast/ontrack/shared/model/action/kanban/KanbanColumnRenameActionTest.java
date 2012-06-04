package br.com.oncast.ontrack.shared.model.action.kanban;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban.KanbanColumnRenameActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnRenameAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;

public class KanbanColumnRenameActionTest extends ModelActionTest {

	private Release release;
	private ProjectContext context;
	private Kanban kanban;

	@Before
	public void setup() throws Exception {
		release = ReleaseTestUtils.createRelease();
		context = mock(ProjectContext.class);
		kanban = mock(Kanban.class);

		when(context.findRelease(release.getId())).thenReturn(release);
		when(context.getKanban(release)).thenReturn(kanban);
	}

	@Test
	public void renameKanbanColumnShouldRenameKanban() throws Exception {
		final String newDescription = "new B";
		final String columnDescription = "B";

		final KanbanColumnRenameAction action = new KanbanColumnRenameAction(release.getId(), columnDescription, newDescription);
		action.execute(context);

		verify(kanban).renameColumn(columnDescription, newDescription);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToRenameKanbanColumnToAEmptyString() throws Exception {
		final String newDescription = "";
		final String columnDescription = "B";

		final KanbanColumnRenameAction action = new KanbanColumnRenameAction(release.getId(), columnDescription, newDescription);
		action.execute(context);

		verify(kanban).renameColumn(columnDescription, newDescription);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void anyErrorOnKanbanRenameShouldBeConvertedToUnableToCompleteActionException() throws Exception {
		final String newDescription = "new B";
		final String columnDescription = "B";

		doThrow(new RuntimeException("any exception")).when(kanban).renameColumn(columnDescription, newDescription);
		final KanbanColumnRenameAction action = new KanbanColumnRenameAction(release.getId(), columnDescription, newDescription);
		action.execute(context);

		verify(kanban).renameColumn(columnDescription, newDescription);
	}

	@Test
	public void shouldUpdateTheProgressOfScopesOfTheReleaseToTheNewDescription() throws Exception {
		final String newDescription = "new B";
		final String columnDescription = "B";

		final Scope scope = ScopeTestUtils.createScope();
		release.addScope(scope);
		when(context.findScope(scope.getId())).thenReturn(scope);

		final Scope scope2 = ScopeTestUtils.createScope();
		scope2.getProgress().setDescription("other");
		release.addScope(scope2);
		when(context.findScope(scope2.getId())).thenReturn(scope2);

		final Scope scope3 = ScopeTestUtils.createScope();
		scope3.getProgress().setDescription(columnDescription);
		release.addScope(scope3);
		when(context.findScope(scope3.getId())).thenReturn(scope3);

		final KanbanColumnRenameAction action = new KanbanColumnRenameAction(release.getId(), columnDescription, newDescription);
		action.execute(context);

		assertEquals(ProgressState.NOT_STARTED.getDescription(), scope.getProgress().getDescription());
		assertEquals("other", scope2.getProgress().getDescription());
		assertEquals(newDescription, scope3.getProgress().getDescription());
	}

	@Test
	public void kanbanShouldBeLockedAfterExecution() throws Exception {
		final KanbanColumnRenameAction action = new KanbanColumnRenameAction(release.getId(), "B", "new B");
		action.execute(context);

		verify(kanban).setLocked(true);
	}

	@Test
	public void undoShouldJustRenameBackTheColumnAndResetTheProgress() throws Exception {
		final String newDescription = "new B";
		final String columnDescription = "B";

		final Scope scope = ScopeTestUtils.createScope();
		scope.getProgress().setDescription(columnDescription);
		release.addScope(scope);
		when(context.findScope(scope.getId())).thenReturn(scope);

		final KanbanColumnRenameAction action = new KanbanColumnRenameAction(release.getId(), columnDescription, newDescription);
		ModelAction currentAction = action;
		int times = 0;
		for (int i = 0; i < 10; i++) {
			if (i % 2 == 0) {
				currentAction = currentAction.execute(context);
				verify(kanban, times(++times)).renameColumn(columnDescription, newDescription);
				assertEquals(newDescription, scope.getProgress().getDescription());
			}
			else {
				currentAction = currentAction.execute(context);
				verify(kanban, times(times)).renameColumn(newDescription, columnDescription);
				assertEquals(columnDescription, scope.getProgress().getDescription());
			}
		}

	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return KanbanColumnRenameActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return KanbanColumnRenameAction.class;
	}

	@Override
	protected ModelAction getInstance() {
		return new KanbanColumnRenameAction(new UUID(), "", "");
	}
}
