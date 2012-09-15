package br.com.oncast.ontrack.shared.model.action.annotation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationVoteActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.AnnotationVoteAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.DeprecationState;
import br.com.oncast.ontrack.shared.model.annotation.exceptions.AnnotationNotFoundException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.AnnotationTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class AnnotationVoteActionTest extends ModelActionTest {

	private User voter;
	private UUID subjectId;
	private Annotation annotation;

	@Before
	public void setUp() throws Exception {
		subjectId = new UUID();
		annotation = AnnotationTestUtils.create();
		voter = UserTestUtils.createUser();

		when(actionContext.getUserEmail()).thenReturn(voter.getEmail());
		when(context.findUser(voter.getEmail())).thenReturn(voter);
		when(context.findAnnotation(subjectId, annotation.getId())).thenReturn(annotation);
	}

	@Test
	public void theReferenceIdShouldBeTheAnnotatedObjectId() throws Exception {
		assertEquals(subjectId, getNewInstance().getReferenceId());
	}

	@Test
	public void shouldGetTheAnnotationWithTheGivenIdFromTheGivenContext() throws Exception {
		executeAction();
		verify(context).findAnnotation(subjectId, annotation.getId());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToCompleteWhenThereIsNoAnnotationWithTheGivenIdOnTheContext() throws Exception {
		when(context.findAnnotation(subjectId, annotation.getId())).thenThrow(new AnnotationNotFoundException(""));
		executeAction();
	}

	@Test
	public void shouldIncrementTheAnnotationsVoteCountByOneOnVote() throws Exception {
		final int previousVoteCount = annotation.getVoteCount();
		executeAction();
		assertEquals(previousVoteCount + 1, annotation.getVoteCount());
	}

	@Test
	public void shouldConsiderOnlyOneVoteFromSameUser() throws Exception {
		final int previousVoteCount = annotation.getVoteCount();
		for (int i = 0; i < 10; i++) {
			executeAction();
			assertEquals(previousVoteCount + 1, annotation.getVoteCount());
		}
	}

	@Test
	public void undoShouldRemoveTheGivenVote() throws Exception {
		final int previousVoteCount = annotation.getVoteCount();

		final ModelAction undoAction = executeAction();
		assertEquals(previousVoteCount + 1, annotation.getVoteCount());

		undoAction.execute(context, actionContext);
		assertEquals(previousVoteCount, annotation.getVoteCount());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToVoteWhenTheAnnotationIsDeprecated() throws Exception {
		annotation.setDeprecation(DeprecationState.DEPRECATED, voter, new Date());
		executeAction();
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return AnnotationVoteActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return AnnotationVoteAction.class;
	}

	@Override
	protected ModelAction getNewInstance() {
		return new AnnotationVoteAction(annotation.getId(), subjectId);
	}

}
