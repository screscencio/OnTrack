package br.com.oncast.ontrack.shared.model.action.team;

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
		executeAction();
		verify(context).addUser(invitee);
	}

	@Test
	public void undoShouldRemoveTheInvitedUserFromContext() throws Exception {
		final ModelAction undoAction = executeAction();

		when(context.findUser(invitee.getId())).thenReturn(invitee);
		undoAction.execute(context, actionContext);
		verify(context).removeUser(invitee);
	}

	@Override
	protected ModelAction getNewInstance() {
		return new TeamInviteAction(invitee);
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
