package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.beans.Scope;

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
		new UpdateScopeAction(rootScope, "new text").execute();
		assertEquals("new text", rootScope.getDescription());
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals("root", rootScope.getDescription());
		final UpdateScopeAction updateScopeAction = new UpdateScopeAction(rootScope, "new text");
		updateScopeAction.execute();
		assertEquals("new text", rootScope.getDescription());
		updateScopeAction.rollback();
		assertEquals("root", rootScope.getDescription());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void ifTheActionNotExecutedCantBeRolledBack() throws UnableToCompleteActionException {
		new UpdateScopeAction(rootScope, "new text").rollback();
	}
}
