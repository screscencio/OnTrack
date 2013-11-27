package br.com.oncast.ontrack.shared.model.action.scope;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeDeclareTimeSpentActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareTimeSpentAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ScopeDeclareTimeSpentActionTest extends ModelActionTest {

	private Float timeSpent;

	private UUID userId;

	private UUID scopeId;

	@Before
	public void setup() throws Exception {
		final UserRepresentation user = UserRepresentationTestUtils.createUser();
		userId = user.getId();
		final Scope scope = ScopeTestUtils.createScope();
		scopeId = scope.getId();

		timeSpent = 3f;

		Mockito.when(actionContext.getUserId()).thenReturn(userId);
		Mockito.when(context.findUser(userId)).thenReturn(user);
	}

	@Test
	public void shouldAddDeclaration() throws Exception {
		executeAction();
		Mockito.verify(context).declareTimeSpent(scopeId, userId, timeSpent);
	}

	@Test
	public void shouldAddDeclarationToTheCurrentUser() throws Exception {
		final UserRepresentation anotherUser = UserRepresentationTestUtils.createUser();
		final UUID anotherUserId = anotherUser.getId();

		Mockito.when(actionContext.getUserId()).thenReturn(anotherUserId);
		Mockito.when(context.findUser(anotherUserId)).thenReturn(anotherUser);

		executeAction();
		Mockito.verify(context).declareTimeSpent(scopeId, anotherUserId, timeSpent);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToDeclareTimeToAnInexistentScope() throws Exception {
		Mockito.when(context.findScope(scopeId)).thenThrow(new ScopeNotFoundException());
		executeAction();
	}

	@Test
	public void undoShouldRemoveDeclarationWhenThereWereNoDeclarationBefore() throws Exception {
		final Float previousDeclaration = null;
		Mockito.when(context.getDeclaredTimeSpent(scopeId, userId)).thenReturn(previousDeclaration);

		final ModelAction undoAction = executeAction();
		undoAction.execute(context, actionContext);

		Mockito.verify(context).declareTimeSpent(scopeId, userId, previousDeclaration);
	}

	@Test
	public void undoShouldDeclareThePreviousValueWhenThereWereADeclarationBefore() throws Exception {
		final Float previousDeclaration = 9F;
		Mockito.when(context.getDeclaredTimeSpent(scopeId, userId)).thenReturn(previousDeclaration);

		final ModelAction undoAction = executeAction();
		undoAction.execute(context, actionContext);

		Mockito.verify(context).declareTimeSpent(scopeId, userId, previousDeclaration);
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ScopeDeclareTimeSpentAction(scopeId, timeSpent);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ScopeDeclareTimeSpentAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ScopeDeclareTimeSpentActionEntity.class;
	}

}
