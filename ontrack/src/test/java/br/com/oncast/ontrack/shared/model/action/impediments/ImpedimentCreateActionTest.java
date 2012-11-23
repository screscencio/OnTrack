package br.com.oncast.ontrack.shared.model.action.impediments;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.impediments.ImpedimentCreateActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.ImpedimentCreateAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.annotation.DeprecationState;
import br.com.oncast.ontrack.shared.model.annotation.exceptions.AnnotationNotFoundException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.AnnotationTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

public class ImpedimentCreateActionTest extends ModelActionTest {

	private UUID subjectId;
	private User user;
	private Annotation annotation;

	@Before
	public void setup() throws Exception {
		subjectId = new UUID();

		user = UserTestUtils.createUser();

		annotation = AnnotationTestUtils.create(user);
		when(context.findAnnotation(subjectId, annotation.getId())).thenReturn(annotation);

		when(actionContext.getUserId()).thenReturn(user.getId());
		when(actionContext.getTimestamp()).thenReturn(new Date());
		when(context.findUser(user.getId())).thenReturn(user);
	}

	@Test
	public void shouldSetTheAnnotationWithImpedimentStatusWhenTheAnnotationAlreadyExists() throws Exception {
		executeAction();

		verify(context, never()).addAnnotation(any(UUID.class), any(Annotation.class));
		assertEquals(AnnotationType.OPEN_IMPEDIMENT, annotation.getType());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToCreateAnImpedimentFromAnInexistentAnnotation() throws Exception {
		when(context.findAnnotation(subjectId, annotation.getId())).thenThrow(new AnnotationNotFoundException(""));

		executeAction();
	}

	@Test
	public void undoShouldReSetTheAnnotationToPreviousState() throws Exception {
		final AnnotationType previousState = annotation.getType();

		final ModelAction undoAction = executeAction();

		undoAction.execute(context, actionContext);

		assertEquals(previousState, annotation.getType());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToCreateImpedimentFromDeprecatedAnnotations() throws Exception {
		annotation.setDeprecation(DeprecationState.DEPRECATED, user, new Date());

		executeAction();
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ImpedimentCreateAction(subjectId, annotation.getId());
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ImpedimentCreateAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ImpedimentCreateActionEntity.class;
	}

}
