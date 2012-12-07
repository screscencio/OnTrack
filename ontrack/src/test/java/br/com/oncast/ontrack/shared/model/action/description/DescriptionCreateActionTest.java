package br.com.oncast.ontrack.shared.model.action.description;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.description.DescriptionCreateActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.DescriptionCreateAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;

public class DescriptionCreateActionTest extends ModelActionTest {

	private UUID subjectId;
	private UserRepresentation author;
	private String message;

	@Before
	public void setUp() throws Exception {
		subjectId = new UUID();
		author = UserRepresentationTestUtils.createUser();
		message = "Any message";

		when(actionContext.getUserId()).thenReturn(author.getId());
		when(context.findUser(author.getId())).thenReturn(author);
	}

	@Test
	public void shouldAssociateTheDescriptionWithTheDescriptionObjectsUUID() throws Exception {
		subjectId = new UUID();
		executeAction();

		verify(context).addDescription(Mockito.any(Description.class), Mockito.eq(subjectId));
	}

	@Test
	public void shouldAssociateTheDescriptionWithTheUser() throws Exception {
		executeAction();

		final ArgumentCaptor<Description> captor = ArgumentCaptor.forClass(Description.class);
		verify(context).addDescription(captor.capture(), Mockito.any(UUID.class));

		assertEquals(author, captor.getValue().getAuthor());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotCompleteWhenTheSpecifiedUserDoesNotExist() throws Exception {
		when(context.findUser(author.getId())).thenThrow(new UserNotFoundException(""));
		executeAction();
	}

	@Test
	public void shouldHaveTheDescriptionMessage() throws Exception {
		executeAction();

		final ArgumentCaptor<Description> captor = ArgumentCaptor.forClass(Description.class);
		verify(context).addDescription(captor.capture(), Mockito.any(UUID.class));

		assertEquals(message, captor.getValue().getDescription());
	}

	@Test
	public void shouldRemoveTheCreatedAnnotationOnUndo() throws Exception {
		final ModelAction undoAction = executeAction();

		final ArgumentCaptor<Description> captor = ArgumentCaptor.forClass(Description.class);
		verify(context).addDescription(captor.capture(), Mockito.any(UUID.class));

		when(context.findDescriptionFor(subjectId)).thenReturn(captor.getValue());

		undoAction.execute(context, actionContext);

		verify(context).removeDescriptionFor(subjectId);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotCompleteWhenMessageIsEmptyOrNull() throws Exception {
		new DescriptionCreateAction(subjectId, "").execute(context, actionContext);
	}

	@Override
	protected ModelAction getNewInstance() {
		return new DescriptionCreateAction(subjectId, message);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return DescriptionCreateAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return DescriptionCreateActionEntity.class;
	}
}
