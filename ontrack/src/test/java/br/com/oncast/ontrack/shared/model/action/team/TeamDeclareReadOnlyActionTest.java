package br.com.oncast.ontrack.shared.model.action.team;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team.TeamDeclareReadOnlyActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.TeamDeclareReadOnlyAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TeamDeclareReadOnlyActionTest extends ModelActionTest {

	private boolean readOnly;

	private UserRepresentation user;

	@Before
	public void setup() {
		user = UserRepresentationTestUtils.createUser();
		readOnly = true;
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToDeclareReadyOnlyForInexistingUser() throws Exception {
		Mockito.when(context.findUser(user.getId())).thenThrow(new UserNotFoundException(""));
		executeAction();
	}

	@Test
	public void changesTheReadOnlyAttributeOfTheGivenUser() throws Exception {
		Mockito.when(context.findUser(user.getId())).thenReturn(user);
		executeAction();
		assertTrue(user.isReadOnly());
	}

	@Test
	public void undoChengesTheReadOnlyAttributeToThepreviousValue() throws Exception {
		Mockito.when(context.findUser(user.getId())).thenReturn(user);
		final boolean previousReadOnly = user.isReadOnly();
		final ModelAction undoAction = executeAction();
		assertTrue(user.isReadOnly());
		undoAction.execute(context, actionContext);
		assertEquals(previousReadOnly, user.isReadOnly());
	}

	@Test
	public void itsOkToUndoAndRedoSeveralTimes() throws Exception {
		Mockito.when(context.findUser(user.getId())).thenReturn(user);

		ModelAction action = getNewInstance();
		for (int i = 0; i < 15; i++) {
			final boolean previousReadOnly = user.isReadOnly();
			action = action.execute(context, actionContext);
			assertEquals(!previousReadOnly, user.isReadOnly());
		}
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void cantSetReadOnlyAttributeOfHimself() throws Exception {
		user = getActionAuthor();
		executeAction();
	}

	@Override
	protected ModelAction getNewInstance() {
		return new TeamDeclareReadOnlyAction(user.getId(), readOnly);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return TeamDeclareReadOnlyAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return TeamDeclareReadOnlyActionEntity.class;
	}

}
