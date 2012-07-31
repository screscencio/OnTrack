package br.com.oncast.ontrack.shared.model.action.checklist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist.ChecklistCreateActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.ChecklistCreateAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;

public class ChecklistCreateActionTest extends ModelActionTest {

	private UUID subjectId;
	private String checklistTitle;

	@Before
	public void setup() {
		subjectId = new UUID();
		checklistTitle = "Some title for checklist";
	}

	@Test
	public void shouldAddChecklistIntoContext() throws Exception {
		execute();
		verify(context).addChecklist(Mockito.any(UUID.class), Mockito.any(Checklist.class));
	}

	@Test
	public void shouldAddChecklistToTheGivenSubjectId() throws Exception {
		execute();
		verify(context).addChecklist(Mockito.eq(subjectId), Mockito.any(Checklist.class));
	}

	@Test
	public void shouldAddChecklistWithTheGivenTitle() throws Exception {
		execute();
		final ArgumentCaptor<Checklist> captor = ArgumentCaptor.forClass(Checklist.class);
		verify(context).addChecklist(Mockito.any(UUID.class), captor.capture());
		assertEquals(checklistTitle, captor.getValue().getTitle());
	}

	@Test
	public void createdChecklistShouldHaveAUuid() throws Exception {
		execute();
		final ArgumentCaptor<Checklist> captor = ArgumentCaptor.forClass(Checklist.class);
		verify(context).addChecklist(Mockito.any(UUID.class), captor.capture());
		assertNotNull(captor.getValue().getId());
	}

	@Test
	public void shouldHaveSameBehaviourNoMatterHowManyTimesItWillBeExecuted() throws Exception {
		final ModelAction action = getNewInstance();

		final int nOfInvocations = 10;
		for (int i = 0; i < nOfInvocations; i++) {
			action.execute(context, actionContext);
		}

		final ArgumentCaptor<Checklist> captor = ArgumentCaptor.forClass(Checklist.class);
		verify(context, Mockito.times(nOfInvocations)).addChecklist(Mockito.any(UUID.class), captor.capture());

		final List<Checklist> allValues = captor.getAllValues();
		assertEquals(nOfInvocations, allValues.size());

		final Checklist firstChecklist = allValues.get(0);
		for (final Checklist checklist : allValues) {
			DeepEqualityTestUtils.assertObjectEquality(firstChecklist, checklist);
		}
	}

	@Test
	public void shouldRemoveTheCreatedChecklistOnUndo() throws Exception {
		final ModelAction undoAction = execute();
		final ArgumentCaptor<Checklist> captor = ArgumentCaptor.forClass(Checklist.class);
		verify(context).addChecklist(Mockito.eq(subjectId), captor.capture());
		final Checklist addedChecklist = captor.getValue();

		when(context.findChecklist(subjectId, addedChecklist.getId())).thenReturn(addedChecklist);
		undoAction.execute(context, actionContext);

		verify(context).removeChecklist(subjectId, addedChecklist);
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ChecklistCreateAction(subjectId, checklistTitle);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ChecklistCreateAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ChecklistCreateActionEntity.class;
	}

}
