package br.com.oncast.ontrack.shared.model.action.team;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team.TeamDeclareCanInviteActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.TeamDeclareCanInviteAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TeamDeclareCanInviteActionTest extends ModelActionTest {

	private boolean canInvite;

	private UserRepresentation user;

	@Before
	public void setUp() throws Exception {
		user = UserRepresentationTestUtils.createUser();
		canInvite = false;
		Mockito.when(context.findUser(user.getId())).thenReturn(user);
		getActionAuthor().setCanInvite(true);
	}

	@Test
	public void changesUserPermissionToInviteOthers() throws Exception {
		assertTrue(user.canInvite());
		executeAction();
		assertFalse(user.canInvite());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void cantChangeAnInexistingUser() throws Exception {
		Mockito.when(context.findUser(user.getId())).thenThrow(new UserNotFoundException(""));
		executeAction();
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToChangeOwnPermission() throws Exception {
		setActionAuthor(user);
		executeAction();
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotbeAbleToDeclareCanInviteIfTheAuthorCantInvite() throws Exception {
		getActionAuthor().setCanInvite(false);
		executeAction();
	}

	@Test
	public void undoShouldSetCanInvitePermissionToPreviousStatus() throws Exception {
		final boolean previousCanInvite = user.canInvite();
		final ModelAction undoAction = executeAction();
		undoAction.execute(context, actionContext);
		assertEquals(previousCanInvite, user.canInvite());
	}

	@Test
	public void itsSafeToExecuteConsecutiveUndoAndRedos() throws Exception {
		boolean canInvite = this.canInvite;
		ModelAction action = getNewInstance();

		for (int i = 0; i < 13; i++) {
			action = action.execute(context, actionContext);
			assertEquals(canInvite, user.canInvite());
			canInvite = !canInvite;
		}
	}

	@Override
	protected ModelAction getNewInstance() {
		return new TeamDeclareCanInviteAction(user.getId(), canInvite);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return TeamDeclareCanInviteAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return TeamDeclareCanInviteActionEntity.class;
	}

}
