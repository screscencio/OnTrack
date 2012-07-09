package br.com.oncast.ontrack.shared.model.action.checklist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist.ChecklistAddItemActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.ChecklistAddItemAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ChecklistAddItemActionTest extends ModelActionTest {

	private UUID checklistId;
	private String itemDescription;
	private UUID subjectId;

	@Mock
	private Checklist checklist;

	@Before
	public void setup() throws Exception {
		checklistId = new UUID();
		subjectId = new UUID();
		itemDescription = "Some description for checklist item";

		when(context.findChecklist(checklistId, subjectId)).thenReturn(checklist);
	}

	@Test
	public void shouldAddTheChecklistItemToChecklistWithTheGivenId() throws Exception {
		execute();
		verify(context).findChecklist(checklistId, subjectId);
		verify(checklist).addItem(Mockito.any(ChecklistItem.class));
	}

	@Test
	public void addedItemShouldHaveTheGivenDescription() throws Exception {
		execute();
		final ArgumentCaptor<ChecklistItem> captor = ArgumentCaptor.forClass(ChecklistItem.class);
		verify(checklist).addItem(captor.capture());
		assertEquals(itemDescription, captor.getValue().getDescription());
	}

	@Test
	public void addedItemSholdHaveTheSameIdNoMatterHowManyTimesTheActionWasExecuted() throws Exception {
		final ModelAction action = getNewInstance();

		final ArgumentCaptor<ChecklistItem> captor = ArgumentCaptor.forClass(ChecklistItem.class);

		final int nOfExecutions = 10;
		for (int i = 0; i < nOfExecutions; i++) {
			action.execute(context, actionContext);
		}
		verify(checklist, Mockito.times(nOfExecutions)).addItem(captor.capture());

		final List<ChecklistItem> capturedValues = captor.getAllValues();
		assertEquals(nOfExecutions, capturedValues.size());

		final UUID generatedId = capturedValues.get(0).getId();
		assertNotNull(generatedId);

		for (final ChecklistItem checklistItem : capturedValues) {
			assertEquals(generatedId, checklistItem.getId());
		}
	}

	@Test
	public void undoShouldRemoveTheAddedItem() throws Exception {
		final ModelAction undoAction = execute();

		final ArgumentCaptor<ChecklistItem> captor = ArgumentCaptor.forClass(ChecklistItem.class);
		verify(checklist).addItem(captor.capture());

		undoAction.execute(context, actionContext);
		verify(checklist).removeItem(captor.getValue().getId());
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ChecklistAddItemAction(checklistId, subjectId, itemDescription);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ChecklistAddItemAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ChecklistAddItemActionEntity.class;
	}

}
