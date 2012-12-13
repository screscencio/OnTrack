package br.com.oncast.ontrack.shared.model.action.team;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team.TeamInviteActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;

public class TeamInviteActionTest extends ModelActionTest {

	private UserRepresentation invitee;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		invitee = UserRepresentationTestUtils.createUser();
	}

	@Test
	public void shouldAddUserToProjectContext() throws Exception {
		when(context.findUser(invitee.getId())).thenThrow(new UserNotFoundException(""));
		executeAction();
		verify(context).addUser(invitee);
	}

	@Test
	public void shouldJustSetAsValidWhenTheUserAlreadyExists() throws Exception {
		invitee.setValid(false);
		when(context.findUser(invitee.getId())).thenReturn(invitee);
		executeAction();
		assertTrue(invitee.isValid());
	}

	@Test
	public void undoShouldRemoveTheInvitedUserFromContext() throws Exception {
		when(context.findUser(invitee.getId())).thenReturn(invitee);
		final ModelAction undoAction = executeAction();

		when(context.findUser(invitee.getId())).thenReturn(invitee);
		undoAction.execute(context, actionContext);

		assertFalse(invitee.isValid());
	}

	@Override
	protected ModelAction getNewInstance() {
		return new TeamInviteAction(invitee.getId());
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return TeamInviteAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return TeamInviteActionEntity.class;
	}

}
