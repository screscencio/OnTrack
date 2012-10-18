package br.com.oncast.ontrack.shared.model.action.impediments;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.impediments.ImpedimentRemoveActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.ImpedimentRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.AnnotationTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class ImpedimentRemoveActionTest extends ModelActionTest {

	private UUID subjectId;
	private User user;
	private Annotation annotation;

	@Before
	public void setup() throws Exception {
		subjectId = new UUID();

		user = UserTestUtils.createUser();

		annotation = AnnotationTestUtils.create(user);
		annotation.setType(AnnotationType.OPEN_IMPEDIMENT, user, new Date());
		when(context.findAnnotation(subjectId, annotation.getId())).thenReturn(annotation);

		when(actionContext.getUserId()).thenReturn(user.getId());
		when(actionContext.getTimestamp()).thenReturn(new Date());
		when(context.findUser(user.getId())).thenReturn(user);
	}

	@Test
	public void shouldSetAnnotationStateToPreviousState() throws Exception {
		executeAction();

		assertEquals(AnnotationType.SIMPLE, annotation.getType());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToRemoveImpedimentWhenAnnotationIsNotImpeded() throws Exception {
		annotation.setType(AnnotationType.SIMPLE, user, new Date());

		executeAction();
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToRemoveImpedimentCreatedByAnotherUser() throws Exception {
		when(actionContext.getUserId()).thenReturn(new UUID());
		executeAction();
	}

	@Test
	public void shouldBeAbleToRemoveImpedimentCreatedBySameUserEvenWhenTheAnnotationWasCreatedByAnotherUser() throws Exception {
		final User annotationAuthor = UserTestUtils.createUser();
		final User impedimentAuthor = UserTestUtils.createUser();

		annotation = AnnotationTestUtils.create(annotationAuthor);
		annotation.setType(AnnotationType.OPEN_IMPEDIMENT, impedimentAuthor, new Date());
		when(context.findAnnotation(subjectId, annotation.getId())).thenReturn(annotation);

		when(actionContext.getUserId()).thenReturn(impedimentAuthor.getId());
		when(actionContext.getTimestamp()).thenReturn(new Date());
		when(context.findUser(impedimentAuthor.getId())).thenReturn(impedimentAuthor);

		executeAction();
	}

	@Test
	public void shouldReturnToPreviousStateWhenThePreviousEvenWhenThePreviousTypeIsntTheDefaultType() throws Exception {
		annotation = AnnotationTestUtils.create(user);
		annotation.setType(AnnotationType.SOLVED_IMPEDIMENT, user, new Date());
		annotation.setType(AnnotationType.OPEN_IMPEDIMENT, user, new Date());
		when(context.findAnnotation(subjectId, annotation.getId())).thenReturn(annotation);

		when(actionContext.getTimestamp()).thenReturn(new Date());

		new ImpedimentRemoveAction(subjectId, annotation.getId(), AnnotationType.SOLVED_IMPEDIMENT).execute(context, actionContext);

		assertEquals(AnnotationType.SOLVED_IMPEDIMENT, annotation.getType());
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ImpedimentRemoveAction(subjectId, annotation.getId(), AnnotationType.SIMPLE);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ImpedimentRemoveAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ImpedimentRemoveActionEntity.class;
	}

}
