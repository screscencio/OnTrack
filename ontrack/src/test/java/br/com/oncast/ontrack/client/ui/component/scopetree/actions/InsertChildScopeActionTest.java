package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.project.Project;
import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class InsertChildScopeActionTest {

	private Scope selectedScope;

	@Before
	public void setUp() {
		selectedScope = new Scope("root");
	}

	@Test
	public void insertChildMustInsertNewChild() throws UnableToCompleteActionException {
		assertEquals(selectedScope.getChildren().size(), 0);
		new ScopeInsertChildAction(selectedScope).execute(new ProjectContext(new Project()));
		assertEquals(1, selectedScope.getChildren().size());
	}

	@Test
	public void theInsertedChildMustBeTheLastChild() throws UnableToCompleteActionException {
		new ScopeInsertChildAction(selectedScope).execute(new ProjectContext(new Project()));
		final ScopeInsertChildAction insertChildScopeAction = new ScopeInsertChildAction(selectedScope);
		assertEquals(1, selectedScope.getChildren().size());
		insertChildScopeAction.execute(new ProjectContext(new Project()));
		final Scope insertedScope = selectedScope.getChildren().get(selectedScope.getChildren().size() - 1);
		assertEquals(insertedScope, insertChildScopeAction.getNewScope());
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(selectedScope.getChildren().size(), 0);
		final ScopeInsertChildAction insertChildScopeAction = new ScopeInsertChildAction(selectedScope);
		insertChildScopeAction.execute(new ProjectContext(new Project()));
		assertEquals(1, selectedScope.getChildren().size());
		insertChildScopeAction.rollback(new ProjectContext(new Project()));
		assertEquals(selectedScope.getChildren().size(), 0);
	}
}
