package br.com.oncast.ontrack.shared.model.action.annotation;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationCreateActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.FileRepresentationTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class AnnotationCreateActionTest extends ModelActionTest {

	private ProjectContext context;
	private User author;
	private UUID annotatedObjectId;
	private String message;
	private ActionContext actionContext;
	private FileRepresentation attachmentFile;

	@Before
	public void setUp() throws Exception {
		context = mock(ProjectContext.class);
		annotatedObjectId = new UUID();
		author = UserTestUtils.createUser();
		attachmentFile = FileRepresentationTestUtils.create();
		message = "Any message";
		actionContext = Mockito.mock(ActionContext.class);

		when(actionContext.getUserEmail()).thenReturn(author.getEmail());
		when(context.findUser(author.getEmail())).thenReturn(author);
	}

	@Test
	public void shouldAssociateTheAnnotationWithTheAnnotatedObjectsUUID() throws Exception {
		annotatedObjectId = new UUID();
		execute();

		verify(context).addAnnotation(Mockito.any(Annotation.class), Mockito.eq(annotatedObjectId));
	}

	@Test
	public void shouldAssociateTheAnnotationWithTheUser() throws Exception {
		execute();

		final ArgumentCaptor<Annotation> captor = ArgumentCaptor.forClass(Annotation.class);
		verify(context).addAnnotation(captor.capture(), Mockito.any(UUID.class));

		assertEquals(author, captor.getValue().getAuthor());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotCompleteWhenTheSpecifiedUserDoesNotExist() throws Exception {
		when(context.findUser(author.getEmail())).thenThrow(new UserNotFoundException(""));
		execute();
	}

	@Test
	public void shouldHaveTheMessage() throws Exception {
		execute();

		final ArgumentCaptor<Annotation> captor = ArgumentCaptor.forClass(Annotation.class);
		verify(context).addAnnotation(captor.capture(), Mockito.any(UUID.class));

		assertEquals(message, captor.getValue().getMessage());
	}

	@Test
	public void shouldRemoveTheCreatedAnnotationOnUndo() throws Exception {
		final ModelAction undoAction = execute();

		final ArgumentCaptor<Annotation> captor = ArgumentCaptor.forClass(Annotation.class);
		verify(context).addAnnotation(captor.capture(), Mockito.any(UUID.class));
		final Annotation createdAnnotation = captor.getValue();

		when(context.findAnnotation(createdAnnotation.getId(), annotatedObjectId)).thenReturn(createdAnnotation);

		undoAction.execute(context, Mockito.mock(ActionContext.class));

		verify(context).removeAnnotation(createdAnnotation, annotatedObjectId);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotCompleteWhenMessageIsEmptyAndThereIsNoAttachedFile() throws Exception {
		new AnnotationCreateAction(annotatedObjectId, "", null).execute(context, actionContext);
	}

	@Test
	public void shouldHaveTheAttachmentFileWhenPresent() throws Exception {
		when(context.findFileRepresentation(attachmentFile.getId())).thenReturn(attachmentFile);
		execute();

		final ArgumentCaptor<Annotation> captor = ArgumentCaptor.forClass(Annotation.class);
		verify(context).addAnnotation(captor.capture(), Mockito.any(UUID.class));

		assertEquals(attachmentFile, captor.getValue().getAttachmentFile());
	}

	@Test
	public void shouldBeAbleToNotHaveAAttachmentFile() throws Exception {
		new AnnotationCreateAction(annotatedObjectId, message, null).execute(context, actionContext);

		final ArgumentCaptor<Annotation> captor = ArgumentCaptor.forClass(Annotation.class);
		verify(context).addAnnotation(captor.capture(), Mockito.any(UUID.class));

		assertNull(captor.getValue().getAttachmentFile());
	}

	private ModelAction execute() throws UnableToCompleteActionException {
		return new AnnotationCreateAction(annotatedObjectId, message, attachmentFile.getId()).execute(context, actionContext);
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return AnnotationCreateActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return AnnotationCreateAction.class;
	}

	@Override
	protected ModelAction getInstance() {
		return new AnnotationCreateAction(annotatedObjectId, message, attachmentFile.getId());
	}

}
