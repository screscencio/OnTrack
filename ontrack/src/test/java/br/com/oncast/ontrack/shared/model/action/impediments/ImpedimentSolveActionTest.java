package br.com.oncast.ontrack.shared.model.action.impediments;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.impediments.ImpedimentSolveActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.ImpedimentSolveAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.annotation.DeprecationState;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.AnnotationTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class ImpedimentSolveActionTest extends ModelActionTest {

	private UUID subjectId;
	private User user;
	private Annotation annotation;
	private Date timestamp;

	@Before
	public void setup() throws Exception {
		subjectId = new UUID();
		user = UserTestUtils.createUser();

		annotation = AnnotationTestUtils.create(user);
		timestamp = new Date();
		when(context.findAnnotation(subjectId, annotation.getId())).thenReturn(annotation);

		when(actionContext.getUserEmail()).thenReturn(user.getEmail());
		when(actionContext.getTimestamp()).thenReturn(new Date());
		when(context.findUser(user.getEmail())).thenReturn(user);
	}

	@Test
	public void shouldSetTheAnnotationWithSolvedImpedimentStatus() throws Exception {
		annotation.setType(AnnotationType.OPEN_IMPEDIMENT, user, timestamp);
		executeAction();

		assertEquals(AnnotationType.SOLVED_IMPEDIMENT, annotation.getType());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToSolveAnAnnotationWithSimpleType() throws Exception {
		annotation.setType(AnnotationType.SIMPLE, user, new Date());

		executeAction();
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToSolveAnAnnotationAlreadySolved() throws Exception {
		annotation.setType(AnnotationType.SOLVED_IMPEDIMENT, user, new Date());

		executeAction();
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToSolveImpedimentFromDeprecatedAnnotations() throws Exception {
		annotation.setDeprecation(DeprecationState.DEPRECATED, user, new Date());

		executeAction();
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ImpedimentSolveAction(subjectId, annotation.getId());
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ImpedimentSolveAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ImpedimentSolveActionEntity.class;
	}

}
