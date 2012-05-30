package br.com.oncast.ontrack.shared.model.action.annotation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationRemoveActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.AnnotationRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.AnnotationTestUtils;

public class AnnotationRemoveActionTest extends ModelActionTest {

	private ProjectContext context;
	private UUID annotatedObjectId;
	private Annotation annotation;

	@Before
	public void setUp() throws Exception {
		context = mock(ProjectContext.class);
		annotatedObjectId = new UUID();
		annotation = AnnotationTestUtils.create();

		when(context.findAnnotation(annotation.getId(), annotatedObjectId)).thenReturn(annotation);
	}

	@Test
	public void shouldRemoveTheAnnotationWithTheGivenId() throws Exception {
		execute();
		verify(context).removeAnnotation(Mockito.eq(annotation), Mockito.any(UUID.class));
	}

	@Test
	public void shouldRemoveTheAnnotationOfTheGivenAnnotatedObjectId() throws Exception {
		execute();
		verify(context).removeAnnotation(Mockito.any(Annotation.class), Mockito.eq(annotatedObjectId));
	}

	@Test
	public void shouldRecreateTheSameAnnotationOnUndo() throws Exception {
		execute().execute(context);
		verify(context).addAnnotation(annotation, annotatedObjectId);
	}

	private ModelAction execute() throws UnableToCompleteActionException {
		return getInstance().execute(context);
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
	protected ModelAction getInstance() {
		return new AnnotationRemoveAction(annotation.getId(), annotatedObjectId);
	}

}
