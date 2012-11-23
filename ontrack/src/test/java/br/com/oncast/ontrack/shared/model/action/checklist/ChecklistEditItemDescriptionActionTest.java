package br.com.oncast.ontrack.shared.model.action.checklist;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist.ChecklistEditItemDescriptionActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.ChecklistEditItemDescriptionAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.checklist.exception.ChecklistNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ChecklistTestUtils;

public class ChecklistEditItemDescriptionActionTest extends ModelActionTest {

	private UUID itemId;
	private String newDescription;
	private UUID checklistId;
	private Checklist checklist;
	private ChecklistItem item;
	private UUID subjectId;

	@Before
	public void setup() throws Exception {
		subjectId = new UUID();
		newDescription = "new Description";

		checklist = ChecklistTestUtils.create();
		checklistId = checklist.getId();
		item = ChecklistTestUtils.createItem();
		checklist.addItem(item);
		itemId = item.getId();

		when(context.findChecklist(subjectId, checklistId)).thenReturn(checklist);
	}

	@Test
	public void shouldSetTheNewDescriptionToTheGivenChecklistItem() throws Exception {
		executeAction();
		assertEquals(newDescription, item.getDescription());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToEditAnItemFromAnInexistentChecklist() throws Exception {
		when(context.findChecklist(subjectId, checklistId)).thenThrow(new ChecklistNotFoundException(""));
		executeAction();
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToEditAnInexistentItemFromAnExistentChecklist() throws Exception {
		checklist.removeItem(itemId);
		executeAction();
	}

	@Test
	public void undoShouldReturnTheChecklistItemsDescriptionToThePreviousDescription() throws Exception {
		final String previousDescription = item.getDescription();

		executeAction().execute(context, actionContext);

		assertEquals(previousDescription, item.getDescription());
	}

	@Test
	public void referenceIdShouldBeTheChecklistItemId() throws Exception {
		assertEquals(itemId, getNewInstance().getReferenceId());
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ChecklistEditItemDescriptionAction(subjectId, checklistId, itemId, newDescription);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ChecklistEditItemDescriptionAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ChecklistEditItemDescriptionActionEntity.class;
	}

}
