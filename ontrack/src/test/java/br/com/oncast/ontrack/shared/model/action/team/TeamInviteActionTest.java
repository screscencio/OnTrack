package br.com.oncast.ontrack.shared.model.action.team;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team.TeamInviteActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TeamInviteActionTest extends ModelActionTest {

	private UserRepresentation invitee;

	@Before
	public void setUp() throws Exception {
		invitee = UserRepresentationTestUtils.createUser();
		final ArrayList<UserRepresentation> usersList = new ArrayList<UserRepresentation>();
		usersList.add(getActionAuthor());
		when(context.getUsers()).thenReturn(usersList);
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

	@Test(expected = UnableToCompleteActionException.class)
	public void canInviteWhenTheActionAuthorDoesNotHavePermissionToDoSo() throws Exception {
		final UserRepresentation user = UserRepresentationTestUtils.createUser();
		user.setCanInvite(false);
		setActionAuthor(user);
		executeAction();
	}

	@Test
	public void canInviteIfIsTheFirstInvitationOfTheProjectEvenWhenTheAuthorDoesNotHaveThePermissionToDoSo() throws Exception {
		when(context.getUsers()).thenReturn(new ArrayList<UserRepresentation>());
		when(context.findUser(invitee.getId())).thenThrow(new UserNotFoundException(""));
		final UserRepresentation user = UserRepresentationTestUtils.createUser();
		user.setCanInvite(false);
		setActionAuthor(user);
		executeAction();
	}

	@Override
	protected ModelAction getNewInstance() {
		return new TeamInviteAction(invitee.getId(), true, false);
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
