package br.com.oncast.ontrack.shared.model.action.annotation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationVoteRemoveActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.AnnotationVoteRemoveAction;
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

public class AnnotationVoteRemoveActionTest extends ModelActionTest {

	private User voter;
	private UUID subjectId;
	private Annotation annotation;

	@Before
	public void setUp() throws Exception {
		voter = UserTestUtils.createUser();
		annotation = AnnotationTestUtils.create();
		subjectId = new UUID();

		when(actionContext.getUserEmail()).thenReturn(voter.getEmail());
		when(context.findUser(voter.getEmail())).thenReturn(voter);
		when(context.findAnnotation(subjectId, annotation.getId())).thenReturn(annotation);
	}

	@Test
	public void theReferenceIdShouldBeTheAnnotatedObjectId() throws Exception {
		assertEquals(subjectId, getNewInstance().getReferenceId());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotCompleteWhenThereIsNoAnnotationWithTheGivenId() throws Exception {
		when(context.findAnnotation(subjectId, annotation.getId())).thenThrow(new AnnotationNotFoundException(""));
		execute();
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToRemoveWhenThereIsNoVoteAtAll() throws Exception {
		assertEquals(0, annotation.getVoteCount());
		execute();
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToRemoveWhenTheRequestingUserWasntTheOneWhoVoted() throws Exception {
		annotation.vote(UserTestUtils.createUser());
		execute();
	}

	@Test
	public void shouldRemoveTheVoteCountByOne() throws Exception {
		annotation.vote(voter);
		final int previousVoteCount = annotation.getVoteCount();
		execute();
		assertEquals(previousVoteCount - 1, annotation.getVoteCount());
	}

	@Test
	public void undoShouldReAddTheRemovedVote() throws Exception {
		annotation.vote(voter);
		final int previousVoteCount = annotation.getVoteCount();
		final ModelAction undoAction = execute();
		assertEquals(previousVoteCount - 1, annotation.getVoteCount());
		undoAction.execute(context, actionContext);
		assertEquals(previousVoteCount, annotation.getVoteCount());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBertAbleToRemoveVoteWhenTheAnnotationIsDeprecated() throws Exception {
		annotation.removeVote(voter);
		annotation.setDeprecation(DeprecationState.DEPRECATED, voter, new Date());
		execute();
	}

	@Override
	protected ModelAction getNewInstance() {
		return new AnnotationVoteRemoveAction(annotation.getId(), subjectId);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return AnnotationVoteRemoveAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return AnnotationVoteRemoveActionEntity.class;
	}

}
