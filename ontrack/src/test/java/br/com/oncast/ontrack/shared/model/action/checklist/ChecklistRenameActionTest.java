package br.com.oncast.ontrack.shared.model.action.checklist;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist.ChecklistRenameActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.ChecklistRenameAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.exception.ChecklistNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ChecklistTestUtils;

public class ChecklistRenameActionTest extends ModelActionTest {

	private UUID checklistId;
	private String newTitle;
	private UUID subjectId;
	private Checklist checklist;

	@Before
	public void setup() throws Exception {
		subjectId = new UUID();
		newTitle = "new Title";

		checklist = ChecklistTestUtils.create();
		checklistId = checklist.getId();

		when(context.findChecklist(subjectId, checklistId)).thenReturn(checklist);
	}

	@Test
	public void shouldSetTheNewTitleToTheGivenChecklist() throws Exception {
		executeAction();
		assertEquals(newTitle, checklist.getTitle());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToRenameAnInexistentChecklist() throws Exception {
		when(context.findChecklist(subjectId, checklistId)).thenThrow(new ChecklistNotFoundException(""));
		executeAction();
	}

	@Test
	public void undoShouldReturnTheChecklistsTitleToThePreviousTitle() throws Exception {
		final String previousTitle = checklist.getTitle();

		executeAction().execute(context, actionContext);

		assertEquals(previousTitle, checklist.getTitle());
	}

	@Test
	public void referenceIdShouldBeTheChecklistId() throws Exception {
		assertEquals(checklistId, getNewInstance().getReferenceId());
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ChecklistRenameAction(subjectId, checklistId, newTitle);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ChecklistRenameAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ChecklistRenameActionEntity.class;
	}

}
