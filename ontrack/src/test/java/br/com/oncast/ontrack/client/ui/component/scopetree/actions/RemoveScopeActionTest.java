package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.beans.Scope;

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
		new RemoveScopeAction(rootScope).execute();
	}

	@Test
	public void aRemovedScopeMustBeRemovedFromFather() throws UnableToCompleteActionException {
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(firstChild, rootScope.getChildren().get(0));
		assertEquals(lastChild, rootScope.getChildren().get(1));
		new RemoveScopeAction(firstChild).execute();
		assertEquals(1, rootScope.getChildren().size());
		assertEquals(lastChild, rootScope.getChildren().get(0));
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(firstChild, rootScope.getChildren().get(0));
		assertEquals(lastChild, rootScope.getChildren().get(1));
		final RemoveScopeAction removeScopeAction = new RemoveScopeAction(firstChild);
		removeScopeAction.execute();
		assertEquals(1, rootScope.getChildren().size());
		assertEquals(lastChild, rootScope.getChildren().get(0));
		removeScopeAction.rollback();
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(firstChild, rootScope.getChildren().get(0));
		assertEquals(lastChild, rootScope.getChildren().get(1));
	}
}
