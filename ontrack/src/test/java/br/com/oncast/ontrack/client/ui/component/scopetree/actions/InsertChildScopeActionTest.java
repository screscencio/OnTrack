package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

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
		new ScopeInsertChildAction(selectedScope).execute();
		assertEquals(1, selectedScope.getChildren().size());
	}

	@Test
	public void theInsertedChildMustBeTheLastChild() throws UnableToCompleteActionException {
		new ScopeInsertChildAction(selectedScope).execute();
		final ScopeInsertChildAction insertChildScopeAction = new ScopeInsertChildAction(selectedScope);
		assertEquals(1, selectedScope.getChildren().size());
		insertChildScopeAction.execute();
		final Scope insertedScope = selectedScope.getChildren().get(selectedScope.getChildren().size() - 1);
		assertEquals(insertedScope, insertChildScopeAction.getNewScope());
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(selectedScope.getChildren().size(), 0);
		final ScopeInsertChildAction insertChildScopeAction = new ScopeInsertChildAction(selectedScope);
		insertChildScopeAction.execute();
		assertEquals(1, selectedScope.getChildren().size());
		insertChildScopeAction.rollback();
		assertEquals(selectedScope.getChildren().size(), 0);
	}
}
