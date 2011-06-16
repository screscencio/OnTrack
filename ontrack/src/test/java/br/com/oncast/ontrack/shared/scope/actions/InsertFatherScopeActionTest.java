package br.com.oncast.ontrack.shared.scope.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.project.Project;
import br.com.oncast.ontrack.shared.project.ProjectContext;
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
		insertFatherScopeAction.execute(new ProjectContext(new Project()));
		assertEquals(childScope.getParent(), insertFatherScopeAction.getNewScopeId());
		assertEquals(rootScope.getChildren().get(0), insertFatherScopeAction.getNewScopeId());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void insertingFatherAtRootNodeMustThrowException() throws UnableToCompleteActionException {
		new ScopeInsertAsFatherAction(rootScope).execute(new ProjectContext(new Project()));
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(childScope.getParent(), rootScope);
		assertEquals(rootScope.getChildren().get(0), childScope);
		final ScopeInsertAsFatherAction insertFatherScopeAction = new ScopeInsertAsFatherAction(childScope);
		insertFatherScopeAction.execute(new ProjectContext(new Project()));
		assertEquals(childScope.getParent(), insertFatherScopeAction.getNewScopeId());
		assertEquals(rootScope.getChildren().get(0), insertFatherScopeAction.getNewScopeId());
		insertFatherScopeAction.rollback(new ProjectContext(new Project()));
		assertEquals(childScope.getParent(), rootScope);
		assertEquals(rootScope.getChildren().get(0), childScope);
	}
}
