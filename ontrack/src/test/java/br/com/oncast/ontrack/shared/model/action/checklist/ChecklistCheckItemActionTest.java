package br.com.oncast.ontrack.shared.model.action.checklist;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist.ChecklistCheckItemActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.ChecklistCheckItemAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ChecklistCheckItemActionTest extends ModelActionTest {

	private UUID itemId;

	private UUID subjectId;

	private UUID checklistId;

	@Mock
	private Checklist checklist;

	@Mock
	private ChecklistItem item;

	@Before
	public void setup() throws Exception {
		itemId = new UUID();
		subjectId = new UUID();
		checklistId = new UUID();
		when(context.findChecklist(subjectId, checklistId)).thenReturn(checklist);
		when(checklist.getItem(itemId)).thenReturn(item);
	}

	@Test
	public void shouldCheckTheGivenChecklistItem() throws Exception {
		execute();

		verify(context).findChecklist(subjectId, checklistId);
		verify(checklist).getItem(itemId);
		verify(item).setChecked(true);
	}

	@Test
	public void shouldUncheckTheGivenItemOnUndo() throws Exception {
		final ModelAction undoAction = execute();
		verify(item).setChecked(true);

		undoAction.execute(context, actionContext);

		verify(item).setChecked(false);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToCheckAnInexistantItem() throws Exception {
		when(checklist.getItem(itemId)).thenReturn(null);
		execute();
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ChecklistCheckItemAction(subjectId, checklistId, itemId);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ChecklistCheckItemAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ChecklistCheckItemActionEntity.class;
	}

}
