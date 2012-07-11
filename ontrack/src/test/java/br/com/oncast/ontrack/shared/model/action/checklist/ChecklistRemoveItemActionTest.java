package br.com.oncast.ontrack.shared.model.action.checklist;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist.ChecklistRemoveItemActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.ChecklistRemoveItemAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.ChecklistTestUtils;

public class ChecklistRemoveItemActionTest extends ModelActionTest {

	@Mock
	private Checklist checklist;

	private UUID checklistId;

	private UUID subjectId;

	private UUID itemId;

	@Before
	public void setup() throws Exception {
		subjectId = new UUID();
		checklistId = new UUID();
		itemId = new UUID();

		when(context.findChecklist(subjectId, checklistId)).thenReturn(checklist);
	}

	@Test
	public void shouldRemoveTheChecklistItemFromTheChecklistWithTheGivenId() throws Exception {
		final ChecklistItem itemMock = mock(ChecklistItem.class);
		when(checklist.removeItem(itemId)).thenReturn(itemMock);
		execute();

		verify(context).findChecklist(subjectId, checklistId);
		verify(checklist).removeItem(itemId);
	}

	@Test
	public void undoShouldReAddTheRemovedItem() throws Exception {
		final String itemDescription = "description";
		final boolean isChecked = true;

		final ChecklistItem item = ChecklistTestUtils.createItem(itemId, itemDescription, isChecked);

		when(checklist.removeItem(itemId)).thenReturn(item);
		final ModelAction undoAction = execute();

		undoAction.execute(context, actionContext);

		final ArgumentCaptor<ChecklistItem> captor = ArgumentCaptor.forClass(ChecklistItem.class);
		verify(checklist).addItem(captor.capture());
		final ChecklistItem addedItem = captor.getValue();

		assertEquals(itemDescription, addedItem.getDescription());
		assertEquals(itemId, addedItem.getId());
		assertEquals(isChecked, addedItem.isChecked());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToRemoveAInexistantItem() throws Exception {
		when(checklist.removeItem(itemId)).thenReturn(null);
		execute();
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ChecklistRemoveItemAction(subjectId, checklistId, itemId);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ChecklistRemoveItemAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ChecklistRemoveItemActionEntity.class;
	}

}
