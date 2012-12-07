package br.com.oncast.ontrack.shared.model.action.description;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.description.DescriptionRemoveActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.DescriptionRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.DescriptionTestUtils;

public class DescriptionRemoveActionTest extends ModelActionTest {

	private UUID subjectId;
	private Description description;

	@Before
	public void setUp() throws Exception {
		subjectId = new UUID();
		description = DescriptionTestUtils.create();

		when(context.findDescriptionFor(subjectId)).thenReturn(description);
		when(actionContext.getUserId()).thenReturn(description.getAuthor().getId());
	}

	@Test
	public void shouldRemoveTheAnnotationWithTheGivenId() throws Exception {
		executeAction();
		verify(context).removeDescriptionFor(Mockito.any(UUID.class));
	}

	@Test
	public void shouldRemoveTheAnnotationOfTheGivenAnnotatedObjectId() throws Exception {
		executeAction();
		verify(context).removeDescriptionFor(Mockito.eq(subjectId));
	}

	@Test
	public void shouldRecreateTheSameDescriptionOnUndo() throws Exception {
		executeAction().execute(context, Mockito.mock(ActionContext.class));
		verify(context).addDescription(description, subjectId);
	}

	@Override
	protected ModelAction getNewInstance() {
		return new DescriptionRemoveAction(subjectId, description.getId(), false);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return DescriptionRemoveAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return DescriptionRemoveActionEntity.class;
	}

}
