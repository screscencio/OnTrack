package br.com.oncast.ontrack.shared.model.action.checklist;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

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
		verify(context).addChecklist(Mockito.any(Checklist.class), Mockito.any(UUID.class));
	}

	@Test
	public void shouldAddChecklistToTheGivenSubjectId() throws Exception {
		execute();
		verify(context).addChecklist(Mockito.any(Checklist.class), Mockito.eq(subjectId));
	}

	@Test
	public void shouldAddChecklistWithTheGivenTitle() throws Exception {
		execute();
		final ArgumentCaptor<Checklist> captor = ArgumentCaptor.forClass(Checklist.class);
		verify(context).addChecklist(captor.capture(), Mockito.any(UUID.class));
		assertEquals(checklistTitle, captor.getValue().getTitle());
	}

	@Test
	public void createdChecklistShouldHaveAUuid() throws Exception {
		execute();
		final ArgumentCaptor<Checklist> captor = ArgumentCaptor.forClass(Checklist.class);
		verify(context).addChecklist(captor.capture(), Mockito.any(UUID.class));
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
		verify(context, Mockito.times(nOfInvocations)).addChecklist(captor.capture(), Mockito.any(UUID.class));

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
		verify(context).addChecklist(captor.capture(), Mockito.eq(subjectId));

		undoAction.execute(context, actionContext);

		verify(context).removeChecklist(captor.getValue().getId(), subjectId);
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
