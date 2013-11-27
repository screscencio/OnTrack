package br.com.oncast.ontrack.shared.model.action.kanban;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban.KanbanLockActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.KanbanLockAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.assertions.KanbanTestUtils;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.when;

public class KanbanLockActionTest extends ModelActionTest {

	private Release release;
	private Kanban kanban;
	private KanbanColumn column1;
	private KanbanColumn column2;

	@Before
	public void setup() throws Exception {
		release = ReleaseTestUtils.createRelease();
		kanban = KanbanTestUtils.createWith();

		column1 = KanbanTestUtils.createColumn("column1");
		column2 = KanbanTestUtils.createColumn("column2");
		kanban.appendColumn(column1);
		kanban.appendColumn(column2);

		when(context.findRelease(release.getId())).thenReturn(release);
		when(context.getKanban(release)).thenReturn(kanban);
	}

	@Test
	public void shouldLockKanban() throws Exception {
		assertFalse(kanban.isLocked());
		executeAction();
		assertTrue(kanban.isLocked());
	}

	@Test
	public void shouldGiveIdToAllCollumns() throws Exception {
		assertNull(column1.getId());
		assertNull(column2.getId());
		executeAction();
		assertNotNull(column1.getId());
		assertNotNull(column2.getId());
	}

	@Test
	public void shouldKeepExistingColumnIds() throws Exception {
		final UUID id = new UUID();
		column2.setId(id);
		executeAction();
		assertEquals(id, column2.getId());
	}

	@Test
	public void executingSeveralTimesShouldSetSameId() throws Exception {
		final ModelAction action = getNewInstance();
		action.execute(context, actionContext);
		final UUID id1 = column1.getId();
		final UUID id2 = column2.getId();
		kanban.setLocked(false);
		column1.setId(null);
		column2.setId(null);
		action.execute(context, actionContext);
		assertEquals(id1, column1.getId());
		assertEquals(id2, column2.getId());
	}

	@Test
	public void shouldNotBeAbleToUndoKanbanUnlockAction() throws Exception {
		final ModelAction undoAction = executeAction();
		assertNull(undoAction);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void whenAKanbanIsAlreadyLockedItShouldNotAllowNewLock() throws Exception {
		executeAction();
		executeAction();
	}

	@Override
	protected ModelAction getNewInstance() {
		return new KanbanLockAction(release.getId());
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return KanbanLockAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return KanbanLockActionEntity.class;
	}

}
