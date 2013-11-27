package br.com.oncast.ontrack.shared.model.action.annotation;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationRemoveActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.AnnotationTestUtils;
import br.com.oncast.ontrack.utils.model.FileRepresentationTestUtils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AnnotationRemoveActionTest extends ModelActionTest {

	private UUID subjectId;
	private Annotation annotation;

	@Before
	public void setUp() throws Exception {
		subjectId = new UUID();
		annotation = AnnotationTestUtils.create();

		when(context.findAnnotation(subjectId, annotation.getId())).thenReturn(annotation);
		when(actionContext.getUserId()).thenReturn(annotation.getAuthor().getId());
	}

	@Test
	public void shouldRemoveTheAnnotationWithTheGivenId() throws Exception {
		executeAction();
		verify(context).removeAnnotation(Mockito.any(UUID.class), Mockito.eq(annotation));
	}

	@Test
	public void shouldRemoveTheAnnotationOfTheGivenAnnotatedObjectId() throws Exception {
		executeAction();
		verify(context).removeAnnotation(Mockito.eq(subjectId), Mockito.any(Annotation.class));
	}

	@Test
	public void shouldRecreateTheSameAnnotationOnUndo() throws Exception {
		executeAction().execute(context, Mockito.mock(ActionContext.class));
		verify(context).addAnnotation(subjectId, annotation);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToRemoveAnnotationsCreatedByOtherUser() throws Exception {
		when(actionContext.getUserId()).thenReturn(new UUID());
		executeAction();
	}

	@Test
	public void shouldRemoveAllSubAnnotationsRelatedToTheAnnotation() throws Exception {
		final List<Annotation> subAnnotationsList = createSubAnnotationsList(3);

		when(context.findAnnotationsFor(annotation.getId())).thenReturn(subAnnotationsList);

		executeAction();
		for (final Annotation subAnnotation : subAnnotationsList) {
			verify(context).removeAnnotation(annotation.getId(), subAnnotation);
		}
	}

	@Test
	public void undoShouldReAddAllRemovedSubAnnotations() throws Exception {
		final List<Annotation> subAnnotationsList = createSubAnnotationsList(3);

		when(context.findAnnotationsFor(annotation.getId())).thenReturn(subAnnotationsList);

		final ModelAction undoAction = executeAction();

		undoAction.execute(context, actionContext);
		for (final Annotation subAnnotation : subAnnotationsList) {
			verify(context).addAnnotation(annotation.getId(), subAnnotation);
		}
	}

	@Test
	public void undoShouldRestoreTheFileRepresentationOfTheAttachment() throws Exception {
		final FileRepresentation fileRepresentation = FileRepresentationTestUtils.create();
		annotation.setAttachmentFile(fileRepresentation);
		final ModelAction undoAction = executeAction();

		when(context.findFileRepresentation(fileRepresentation.getId())).thenReturn(fileRepresentation);
		undoAction.execute(context, actionContext);

		final ArgumentCaptor<Annotation> captor = ArgumentCaptor.forClass(Annotation.class);
		verify(context).addAnnotation(Mockito.eq(subjectId), captor.capture());

		assertEquals(fileRepresentation, captor.getValue().getAttachmentFile());
	}

	private List<Annotation> createSubAnnotationsList(final int quantity) throws Exception {
		final List<Annotation> list = new ArrayList<Annotation>();
		for (int i = 0; i < quantity; i++) {
			final Annotation subAnnotation = AnnotationTestUtils.create();
			when(context.findAnnotation(annotation.getId(), subAnnotation.getId())).thenReturn(subAnnotation);
			list.add(subAnnotation);
		}
		return list;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return AnnotationRemoveActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return AnnotationRemoveAction.class;
	}

	@Override
	protected ModelAction getNewInstance() {
		return new AnnotationRemoveAction(subjectId, annotation.getId());
	}

}
