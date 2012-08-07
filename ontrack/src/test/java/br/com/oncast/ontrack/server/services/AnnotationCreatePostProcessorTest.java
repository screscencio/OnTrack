package br.com.oncast.ontrack.server.services;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.AnnotationTestUtils;

public class AnnotationCreatePostProcessorTest {

	@Mock
	private AnnotationCreateAction action;

	@Mock
	private PersistenceService persistenceService;

	@Mock
	private ProjectContext context;

	@Mock
	private ActionContext actionContext;

	private Annotation annotation;

	private UUID subjectId;

	private UUID projectId;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);

		annotation = AnnotationTestUtils.create();
		subjectId = new UUID();
		projectId = new UUID();

		when(action.getReferenceId()).thenReturn(subjectId);
		when(action.getAnnotation(context, actionContext)).thenReturn(annotation);
		final ProjectRepresentation projectRepresentationMock = mock(ProjectRepresentation.class);
		when(projectRepresentationMock.getId()).thenReturn(projectId);
		when(context.getProjectRepresentation()).thenReturn(projectRepresentationMock);
	}

	@Test
	public void shouldPersistCreatedAnnotation() throws Exception {
		postProcess();
		verify(persistenceService).persistOrUpdateAnnotation(any(UUID.class), any(UUID.class), eq(annotation));
	}

	@Test
	public void shouldPersistAnnotationsSubjectId() throws Exception {
		postProcess();
		verify(persistenceService).persistOrUpdateAnnotation(any(UUID.class), eq(subjectId), any(Annotation.class));
	}

	@Test
	public void shouldPersistAnnotationsProjectId() throws Exception {
		postProcess();
		verify(persistenceService).persistOrUpdateAnnotation(eq(projectId), any(UUID.class), any(Annotation.class));
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldThrowUnableToCompleteActionExceptionWhenAnnotationCreationFails() throws Exception {
		when(action.getAnnotation(context, actionContext)).thenThrow(new UnableToCompleteActionException(""));
		postProcess();
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldThrowUnableToCompleteActionExceptionWhenPersitenceFails() throws Exception {
		doThrow(new PersistenceException()).when(persistenceService).persistOrUpdateAnnotation(any(UUID.class), any(UUID.class), any(Annotation.class));
		postProcess();
	}

	private void postProcess() throws UnableToCompleteActionException {
		new AnnotationCreatePostProcessor().process(persistenceService, context, actionContext, action);
	}

}
