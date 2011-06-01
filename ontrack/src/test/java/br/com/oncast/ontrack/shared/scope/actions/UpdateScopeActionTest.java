package br.com.oncast.ontrack.shared.scope.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.project.Project;
import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class UpdateScopeActionTest {

	private Scope rootScope;
	private Scope firstChild;

	@Before
	public void setUp() {
		rootScope = new Scope("root");
		firstChild = new Scope("first");
		rootScope.add(firstChild);
	}

	@Test
	public void updateActionChangeScopeDescription() throws UnableToCompleteActionException {
		assertEquals("root", rootScope.getDescription());
		new ScopeUpdateAction(rootScope, "new text").execute(new ProjectContext(new Project()));
		assertEquals("new text", rootScope.getDescription());
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals("root", rootScope.getDescription());
		final ScopeUpdateAction updateScopeAction = new ScopeUpdateAction(rootScope, "new text");
		updateScopeAction.execute(new ProjectContext(new Project()));
		assertEquals("new text", rootScope.getDescription());
		updateScopeAction.rollback(new ProjectContext(new Project()));
		assertEquals("root", rootScope.getDescription());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void ifTheActionNotExecutedCantBeRolledBack() throws UnableToCompleteActionException {
		new ScopeUpdateAction(rootScope, "new text").rollback(new ProjectContext(new Project()));
	}
}
