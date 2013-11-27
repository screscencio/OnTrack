package br.com.oncast.ontrack.shared.model.action.scope;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeMoveToActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveToAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.when;

public class ScopeMoveToActionTest extends ModelActionTest {

	private int index;
	private Scope futureParent;
	private Scope scope;
	private Scope previousParent;

	@Before
	public void setup() throws Exception {
		previousParent = ScopeTestUtils.createScope();
		for (int i = 0; i < 6; i++) {
			previousParent.add(ScopeTestUtils.createScope());
		}

		futureParent = ScopeTestUtils.createScope();
		for (int i = 0; i < 4; i++) {
			futureParent.add(ScopeTestUtils.createScope());
		}

		scope = ScopeTestUtils.createScope();
		previousParent.add(5, scope);

		index = 3;

		when(context.findScope(scope.getId())).thenReturn(scope);
		when(context.findScope(futureParent.getId())).thenReturn(futureParent);
		when(context.findScope(previousParent.getId())).thenReturn(previousParent);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToMoveAnInexitentScope() throws Exception {
		when(context.findScope(scope.getId())).thenThrow(new ScopeNotFoundException());
		executeAction();
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToMoveToAnInexitentParent() throws Exception {
		when(context.findScope(futureParent.getId())).thenThrow(new ScopeNotFoundException());
		executeAction();
	}

	@Test
	public void shouldRemoveTheMovingScopeFromPreviousLocation() throws Exception {
		executeAction();
		assertEquals(-1, previousParent.getChildIndex(scope));
	}

	@Test
	public void shouldAddTheMovingScopeToTheFutureParent() throws Exception {
		executeAction();
		assertEquals(futureParent, scope.getParent());
	}

	@Test
	public void shouldAddTheMovingScopeInTheGivenIndex() throws Exception {
		executeAction();
		assertEquals(index, futureParent.getChildIndex(scope));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void shouldNotBeAbleToMoveTheScopeToAnIndexBiggerThanTheChildremCount() throws Exception {
		index = 99;
		executeAction();
	}

	@Test
	public void shouldBeAbleToMoveTheIndexInSameParentWhenTheScopesPreviousIndexIsBiggerThanTheRequested() throws Exception {
		futureParent.add(scope);
		executeAction();
		assertEquals(index, futureParent.getChildIndex(scope));
	}

	@Test
	public void shouldMoveToTheIndexBeforeWhenThePreviousIndexIsLowerThanTheRequestedIndex() throws Exception {
		futureParent.add(0, scope);
		executeAction();
		assertEquals(index - 1, futureParent.getChildIndex(scope));
	}

	@Test
	public void shouldBeAbleToMoveToTheEndEvenWhenThereIsOnlyTheScopeToBeMoved() throws Exception {
		futureParent.clearChildren();
		futureParent.add(scope);
		index = 1;
		executeAction();
		assertEquals(0, futureParent.getChildIndex(scope));
	}

	@Test
	public void undoShouldMoveBackToPreviousPosition() throws Exception {
		final int previousIndex = previousParent.getChildIndex(scope);

		final ModelAction undoAction = executeAction();
		undoAction.execute(context, actionContext);

		assertEquals(previousIndex, previousParent.getChildIndex(scope));
	}

	@Test
	public void undoShouldHandleSameParentSingleChildMove() throws Exception {
		futureParent.clearChildren();
		futureParent.add(scope);
		index = 1;

		ModelAction action = getNewInstance();
		for (int i = 0; i < 5; i++) {
			action = action.execute(context, actionContext);
			assertEquals(0, futureParent.getChildIndex(scope));
		}
	}

	@Test
	public void undoShouldHandleSameParentLowerIndexMove() throws Exception {
		futureParent.add(0, scope);

		ModelAction action = getNewInstance();
		for (int i = 0; i < 5; i++) {
			final ModelAction undoAction = action.execute(context, actionContext);
			assertEquals(index - 1, futureParent.getChildIndex(scope));

			action = undoAction.execute(context, actionContext);
			assertEquals(0, futureParent.getChildIndex(scope));
		}
	}

	@Test
	public void undoShouldHandleSameParentBiggerIndexMove() throws Exception {
		futureParent.add(scope);
		final int previousIndex = futureParent.getChildIndex(scope);

		ModelAction action = getNewInstance();
		for (int i = 0; i < 5; i++) {
			final ModelAction undoAction = action.execute(context, actionContext);
			assertEquals(index, futureParent.getChildIndex(scope));

			action = undoAction.execute(context, actionContext);
			assertEquals(previousIndex, futureParent.getChildIndex(scope));
		}
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ScopeMoveToAction(scope.getId(), futureParent.getId(), index);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ScopeMoveToAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ScopeMoveToActionEntity.class;
	}

}
