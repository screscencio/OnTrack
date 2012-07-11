package br.com.oncast.ontrack.shared.model.action.annotation;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationRemoveActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.AnnotationTestUtils;

public class AnnotationRemoveActionTest extends ModelActionTest {

	private UUID subjectId;
	private Annotation annotation;

	@Before
	public void setUp() throws Exception {
		subjectId = new UUID();
		annotation = AnnotationTestUtils.create();

		when(context.findAnnotation(annotation.getId(), subjectId)).thenReturn(annotation);
		when(actionContext.getUserEmail()).thenReturn(annotation.getAuthor().getEmail());
	}

	@Test
	public void shouldRemoveTheAnnotationWithTheGivenId() throws Exception {
		execute();
		verify(context).removeAnnotation(Mockito.eq(annotation), Mockito.any(UUID.class));
	}

	@Test
	public void shouldRemoveTheAnnotationOfTheGivenAnnotatedObjectId() throws Exception {
		execute();
		verify(context).removeAnnotation(Mockito.any(Annotation.class), Mockito.eq(subjectId));
	}

	@Test
	public void shouldRecreateTheSameAnnotationOnUndo() throws Exception {
		execute().execute(context, Mockito.mock(ActionContext.class));
		verify(context).addAnnotation(annotation, subjectId);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToRemoveAnnotationsCreatedByOtherUser() throws Exception {
		when(actionContext.getUserEmail()).thenReturn("Another user's e-mail");
		execute();
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
