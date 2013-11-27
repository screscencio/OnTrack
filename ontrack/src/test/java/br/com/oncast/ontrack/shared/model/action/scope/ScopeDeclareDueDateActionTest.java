package br.com.oncast.ontrack.shared.model.action.scope;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeDeclareDueDateActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareDueDateAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import static org.mockito.Mockito.when;

public class ScopeDeclareDueDateActionTest extends ModelActionTest {

	private Date dueDate;
	private UUID scopeId;
	private Scope scope;

	@Before
	public void setup() throws Exception {
		scope = ScopeTestUtils.createScope();
		scopeId = scope.getId();
		dueDate = new Date();
		when(context.findScope(scopeId)).thenReturn(scope);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToDeclareDueDateToAnInexistentScope() throws Exception {
		when(context.findScope(scopeId)).thenThrow(new ScopeNotFoundException());
		executeAction();
	}

	@Test
	public void shouldSetTheScopeDueDateToTheGivenDate() throws Exception {
		executeAction();
		assertEquals(dueDate, scope.getDueDate());
	}

	@Test
	public void shouldBeAbleToRemoveTheDueDateByGivingNullAsDueDate() throws Exception {
		scope.setDueDate(dueDate);
		dueDate = null;
		executeAction();
		assertNull(scope.getDueDate());
	}

	@Test
	public void undoShouldRemoveTheDueDateWhenThereWereNoDueDateSettedBefore() throws Exception {
		final ModelAction undoAction = executeAction();
		undoAction.execute(context, actionContext);
		assertNull(scope.getDueDate());
	}

	@Test
	public void undoShouldSetThePreviousDueDate() throws Exception {
		final Date previousDueDate = new Date(12345);
		scope.setDueDate(previousDueDate);

		final ModelAction undoAction = executeAction();
		undoAction.execute(context, actionContext);

		assertEquals(previousDueDate, scope.getDueDate());
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ScopeDeclareDueDateAction(scopeId, dueDate);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ScopeDeclareDueDateAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ScopeDeclareDueDateActionEntity.class;
	}

}
