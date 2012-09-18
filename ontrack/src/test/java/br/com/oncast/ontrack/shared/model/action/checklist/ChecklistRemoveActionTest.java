package br.com.oncast.ontrack.shared.model.action.checklist;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist.ChecklistRemoveActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.ChecklistRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.checklist.exception.ChecklistNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.ChecklistTestUtils;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;

public class ChecklistRemoveActionTest extends ModelActionTest {

	private UUID subjectId;
	private UUID checklistId;

	@Mock
	private Checklist checklist;

	@Before
	public void setup() throws Exception {
		subjectId = new UUID();
		checklistId = new UUID();
		when(context.findChecklist(subjectId, checklistId)).thenReturn(checklist);
	}

	@Test
	public void shouldRemoveChecklistFromContext() throws Exception {
		executeAction();
		verify(context).removeChecklist(Mockito.any(UUID.class), Mockito.any(Checklist.class));
	}

	@Test
	public void shouldRemoveChecklistFromTheGivenSubjectId() throws Exception {
		executeAction();
		verify(context).removeChecklist(Mockito.eq(subjectId), Mockito.any(Checklist.class));
	}

	@Test
	public void shouldRemoveChecklistWithTheGivenId() throws Exception {
		executeAction();
		verify(context).removeChecklist(Mockito.any(UUID.class), Mockito.eq(checklist));
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToRemoveAnInexistantChecklist() throws Exception {
		when(context.findChecklist(subjectId, checklistId)).thenThrow(new ChecklistNotFoundException(""));
		executeAction();
	}

	@Test
	public void undoShouldReAddAChecklistWithSameChecklist() throws Exception {
		final String checklistTitle = "title";
		when(checklist.getId()).thenReturn(checklistId);
		when(checklist.getTitle()).thenReturn(checklistTitle);
		when(checklist.getItems()).thenReturn(new ArrayList<ChecklistItem>());

		final ModelAction undoAction = executeAction();

		undoAction.execute(context, actionContext);

		final ArgumentCaptor<Checklist> captor = ArgumentCaptor.forClass(Checklist.class);
		verify(context).addChecklist(Mockito.eq(subjectId), captor.capture());
		final Checklist captured = captor.getValue();

		assertEquals(checklistId, captured.getId());
		assertEquals(checklistTitle, captured.getTitle());
	}

	@Test
	public void undoShouldReAddAChecklistWithSameItems() throws Exception {
		final List<ChecklistItem> checklistItems = new LinkedList<ChecklistItem>();
		checklistItems.add(ChecklistTestUtils.createItem());
		checklistItems.add(ChecklistTestUtils.createItem());

		when(checklist.getId()).thenReturn(checklistId);
		when(checklist.getItems()).thenReturn(checklistItems);

		final ModelAction undoAction = executeAction();

		final Checklist recreatedChecklist = mock(Checklist.class);
		when(context.findChecklist(subjectId, checklistId)).thenReturn(recreatedChecklist);
		undoAction.execute(context, actionContext);

		final ArgumentCaptor<ChecklistItem> captor = ArgumentCaptor.forClass(ChecklistItem.class);
		verify(recreatedChecklist, times(checklistItems.size())).addItem(captor.capture());

		DeepEqualityTestUtils.assertObjectEquality(checklistItems, captor.getAllValues());
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ChecklistRemoveAction(subjectId, checklistId);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ChecklistRemoveAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ChecklistRemoveActionEntity.class;
	}

}
