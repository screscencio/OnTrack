package br.com.oncast.ontrack.shared.model.action.annotation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationCreateActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class AnnotationCreateActionTest extends ModelActionTest {

	private ProjectContext context;
	private UUID annotationId;
	private User author;
	private UUID annotatedObjectId;

	@Before
	public void setUp() {
		context = mock(ProjectContext.class);
		annotationId = new UUID();
		annotatedObjectId = new UUID();
		author = UserTestUtils.createUser();
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

	private void execute() throws UnableToCompleteActionException {
		new AnnotationCreateAction(annotatedObjectId, author).execute(context);
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
		return new AnnotationCreateAction(annotatedObjectId, author);
	}

}
