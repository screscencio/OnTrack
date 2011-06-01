package br.com.oncast.ontrack.shared.scope.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.project.Project;
import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class RemoveScopeActionTest {

	private Scope rootScope;
	private Scope firstChild;
	private Scope lastChild;

	@Before
	public void setUp() {
		rootScope = new Scope("root");
		firstChild = new Scope("child");
		lastChild = new Scope("last");
		rootScope.add(firstChild);
		rootScope.add(lastChild);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void rootScopeCantBeRemoved() throws UnableToCompleteActionException {
		new ScopeRemoveAction(rootScope).execute(new ProjectContext(new Project()));
	}

	@Test
	public void aRemovedScopeMustBeRemovedFromFather() throws UnableToCompleteActionException {
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(firstChild, rootScope.getChildren().get(0));
		assertEquals(lastChild, rootScope.getChildren().get(1));
		new ScopeRemoveAction(firstChild).execute(new ProjectContext(new Project()));
		assertEquals(1, rootScope.getChildren().size());
		assertEquals(lastChild, rootScope.getChildren().get(0));
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(firstChild, rootScope.getChildren().get(0));
		assertEquals(lastChild, rootScope.getChildren().get(1));
		final ScopeRemoveAction removeScopeAction = new ScopeRemoveAction(firstChild);
		removeScopeAction.execute(new ProjectContext(new Project()));
		assertEquals(1, rootScope.getChildren().size());
		assertEquals(lastChild, rootScope.getChildren().get(0));
		removeScopeAction.rollback(new ProjectContext(new Project()));
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(firstChild, rootScope.getChildren().get(0));
		assertEquals(lastChild, rootScope.getChildren().get(1));
	}
}
