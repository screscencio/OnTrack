package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.beans.Scope;

public class InsertChildScopeActionTest {

	private Scope selectedScope;

	@Before
	public void setUp() {
		selectedScope = new Scope("root");
	}

	@Test
	public void insertChildMustInsertNewChild() throws UnableToCompleteActionException {
		assertEquals(selectedScope.getChildren().size(), 0);
		new InsertChildScopeAction(selectedScope).execute();
		assertEquals(1, selectedScope.getChildren().size());
	}

	@Test
	public void theInsertedChildMustBeTheLastChild() throws UnableToCompleteActionException {
		new InsertChildScopeAction(selectedScope).execute();
		final InsertChildScopeAction insertChildScopeAction = new InsertChildScopeAction(selectedScope);
		assertEquals(1, selectedScope.getChildren().size());
		insertChildScopeAction.execute();
		final Scope insertedScope = insertChildScopeAction.getScope().getChildren().get(insertChildScopeAction.getScope().getChildren().size() - 1);
		assertEquals(insertedScope, insertChildScopeAction.getNewScope());
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(selectedScope.getChildren().size(), 0);
		final InsertChildScopeAction insertChildScopeAction = new InsertChildScopeAction(selectedScope);
		insertChildScopeAction.execute();
		assertEquals(1, selectedScope.getChildren().size());
		insertChildScopeAction.rollback();
		assertEquals(selectedScope.getChildren().size(), 0);
	}
}
