package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertAsFatherAction;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class InsertFatherScopeActionTest {

	private Scope rootScope;
	private Scope childScope;

	@Before
	public void setUp() {
		rootScope = new Scope("root");
		childScope = new Scope("child");
		rootScope.add(childScope);
	}

	@Test
	public void mustInsertAScopeAsFather() throws UnableToCompleteActionException {
		assertEquals(childScope.getParent(), rootScope);
		assertEquals(rootScope.getChildren().get(0), childScope);
		final ScopeInsertAsFatherAction insertFatherScopeAction = new ScopeInsertAsFatherAction(childScope);
		insertFatherScopeAction.execute();
		assertEquals(childScope.getParent(), insertFatherScopeAction.getNewScope());
		assertEquals(rootScope.getChildren().get(0), insertFatherScopeAction.getNewScope());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void insertingFatherAtRootNodeMustThrowException() throws UnableToCompleteActionException {
		new ScopeInsertAsFatherAction(rootScope).execute();
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(childScope.getParent(), rootScope);
		assertEquals(rootScope.getChildren().get(0), childScope);
		final ScopeInsertAsFatherAction insertFatherScopeAction = new ScopeInsertAsFatherAction(childScope);
		insertFatherScopeAction.execute();
		assertEquals(childScope.getParent(), insertFatherScopeAction.getNewScope());
		assertEquals(rootScope.getChildren().get(0), insertFatherScopeAction.getNewScope());
		insertFatherScopeAction.rollback();
		assertEquals(childScope.getParent(), rootScope);
		assertEquals(rootScope.getChildren().get(0), childScope);
	}
}
