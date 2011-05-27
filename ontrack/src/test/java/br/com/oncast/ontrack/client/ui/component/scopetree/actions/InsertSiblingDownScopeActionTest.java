package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.project.Project;
import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertSiblingDownAction;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class InsertSiblingDownScopeActionTest {

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

	@Test
	public void siblingDownMustBeDown() throws UnableToCompleteActionException {
		assertEquals(firstChild.getParent().getChildren().get(firstChild.getParent().getChildIndex(firstChild) + 1), lastChild);
		assertEquals(2, rootScope.getChildren().size());
		final ScopeInsertSiblingDownAction insertSiblingDownScopeAction = new ScopeInsertSiblingDownAction(firstChild);
		insertSiblingDownScopeAction.execute(new ProjectContext(new Project()));
		assertEquals(3, rootScope.getChildren().size());
		assertEquals(firstChild.getParent().getChildren().get(firstChild.getParent().getChildIndex(firstChild) + 1), insertSiblingDownScopeAction.getNewScope());
		assertEquals(firstChild.getParent().getChildren().get(firstChild.getParent().getChildIndex(firstChild) + 2), lastChild);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void rootCantAddSiblingDown() throws UnableToCompleteActionException {
		new ScopeInsertSiblingDownAction(rootScope).execute(new ProjectContext(new Project()));
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(firstChild.getParent().getChildren().get(firstChild.getParent().getChildIndex(firstChild) + 1), lastChild);
		assertEquals(2, rootScope.getChildren().size());
		final ScopeInsertSiblingDownAction insertSiblingDownScopeAction = new ScopeInsertSiblingDownAction(firstChild);
		insertSiblingDownScopeAction.execute(new ProjectContext(new Project()));
		assertEquals(3, rootScope.getChildren().size());
		assertEquals(firstChild.getParent().getChildren().get(firstChild.getParent().getChildIndex(firstChild) + 1), insertSiblingDownScopeAction.getNewScope());
		assertEquals(firstChild.getParent().getChildren().get(firstChild.getParent().getChildIndex(firstChild) + 2), lastChild);
		insertSiblingDownScopeAction.rollback(new ProjectContext(new Project()));
		assertEquals(firstChild.getParent().getChildren().get(firstChild.getParent().getChildIndex(firstChild) + 1), lastChild);
		assertEquals(2, rootScope.getChildren().size());
	}

}
