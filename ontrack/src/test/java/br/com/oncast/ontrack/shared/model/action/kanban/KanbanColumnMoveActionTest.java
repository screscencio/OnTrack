package br.com.oncast.ontrack.shared.model.action.kanban;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban.KanbanColumnMoveActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnMoveAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.kanban.KanbanFactory;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.ReleaseTestUtils;

public class KanbanColumnMoveActionTest extends ModelActionTest {

	private final String kanbanColumnDescription = "target column";
	private Kanban kanban;
	private ProjectContext context;
	private UUID releaseId;

	@Before
	public void setup() throws Exception {
		final Release release = ReleaseTestUtils.createRelease();
		releaseId = release.getId();
		kanban = KanbanFactory.createFor(release);
		kanban.appendColumn("First Column");
		kanban.appendColumn(kanbanColumnDescription);

		context = mock(ProjectContext.class);
		Mockito.when(context.findRelease(release.getId())).thenReturn(release);
		Mockito.when(context.getKanban(release)).thenReturn(kanban);
	}

	@Test
	public void shouldMoveColumnToDesiredIndex() throws Exception {
		final int desiredIndex = 0;

		final ModelAction action = new KanbanColumnMoveAction(releaseId, kanbanColumnDescription, desiredIndex);
		action.execute(context, Mockito.mock(ActionContext.class));

		assertEquals(desiredIndex, kanban.indexOf(kanbanColumnDescription));
	}

	@Test
	public void shouldLockKanban() throws Exception {
		final ModelAction action = new KanbanColumnMoveAction(releaseId, kanbanColumnDescription, 0);
		action.execute(context, Mockito.mock(ActionContext.class));

		assertTrue(kanban.isLocked());
	}

	@Test
	public void undoShouldReturnKanbanToPreviousState() throws Exception {
		final ModelAction action = new KanbanColumnMoveAction(releaseId, kanbanColumnDescription, 0);
		final ModelAction undo = action.execute(context, Mockito.mock(ActionContext.class));
		undo.execute(context, Mockito.mock(ActionContext.class));
		assertEquals(1, kanban.indexOf(kanbanColumnDescription));
	}

	@Test
	public void shouldRedo() throws Exception {
		final ModelAction action = new KanbanColumnMoveAction(releaseId, kanbanColumnDescription, 0);
		final ModelAction redo = action.execute(context, Mockito.mock(ActionContext.class)).execute(context, Mockito.mock(ActionContext.class));
		redo.execute(context, Mockito.mock(ActionContext.class));
		assertEquals(0, kanban.indexOf(kanbanColumnDescription));
	}

	@Test
	public void undoShouldKeepTheTheKanbanLocked() throws Exception {
		final ModelAction action = new KanbanColumnMoveAction(releaseId, kanbanColumnDescription, 0);
		final ModelAction undo = action.execute(context, Mockito.mock(ActionContext.class));
		undo.execute(context, Mockito.mock(ActionContext.class));
		assertTrue(kanban.isLocked());
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return KanbanColumnMoveActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return KanbanColumnMoveAction.class;
	}

	@Override
	protected ModelAction getInstance() {
		return new KanbanColumnMoveAction(new UUID(), kanbanColumnDescription, 1);
	}
}