package br.com.oncast.ontrack.shared.scope.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertFatherAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class InsertFatherScopeActionTest {

	private Scope rootScope;
	private Scope childScope;
	private ProjectContext context;
	private String newScopeDescription;
	private String newReleaseDescription;

	@Before
	public void setUp() {
		rootScope = new Scope("root");
		childScope = new Scope("child");
		rootScope.add(childScope);

		newScopeDescription = "description for new scope";
		newReleaseDescription = "Release1";

		context = new ProjectContext(new Project(rootScope, new Release("")));
	}

	@Test
	public void mustInsertAScopeAsFather() throws UnableToCompleteActionException {
		assertEquals(childScope.getParent(), rootScope);
		assertEquals(rootScope.getChildren().get(0), childScope);

		new ScopeInsertFatherAction(childScope, newScopeDescription).execute(context);

		assertEquals(childScope.getParent().getDescription(), newScopeDescription);
		assertEquals(rootScope.getChildren().get(0).getDescription(), newScopeDescription);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void insertingFatherAtRootNodeMustThrowException() throws UnableToCompleteActionException {
		new ScopeInsertFatherAction(rootScope, newScopeDescription).execute(context);
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		final ScopeInsertFatherAction insertFatherScopeAction = new ScopeInsertFatherAction(childScope, newScopeDescription);
		insertFatherScopeAction.execute(context);

		assertEquals(childScope.getParent().getDescription(), newScopeDescription);
		assertEquals(rootScope.getChildren().get(0).getDescription(), newScopeDescription);

		insertFatherScopeAction.rollback(context);

		assertEquals(childScope.getParent(), rootScope);
		assertEquals(rootScope.getChildren().get(0), childScope);
	}

	@Test
	public void mustAssociateScopeWithARelease() throws UnableToCompleteActionException {
		new ScopeInsertFatherAction(childScope, newScopeDescription + " @" + newReleaseDescription).execute(context);

		assertEquals(childScope.getParent().getDescription(), newScopeDescription);
		assertEquals(childScope.getParent().getRelease().getDescription(), newReleaseDescription);
		assertEquals(rootScope.getChildren().get(0).getDescription(), newScopeDescription);
	}

	@Test
	public void mustDisassociateScopeFromReleaseAfterUndo() throws UnableToCompleteActionException {
		final ScopeInsertFatherAction insertFatherScopeAction = new ScopeInsertFatherAction(childScope, newScopeDescription + " @" + newReleaseDescription);
		insertFatherScopeAction.execute(context);

		final Scope insertedParent = childScope.getParent();
		final Release release = insertedParent.getRelease();

		assertEquals(release.getDescription(), newReleaseDescription);
		assertTrue(release.getScopeList().contains(insertedParent));
		assertEquals(insertedParent.getDescription(), newScopeDescription);
		assertEquals(rootScope.getChildren().get(0).getDescription(), newScopeDescription);

		insertFatherScopeAction.rollback(context);

		assertEquals(childScope.getParent(), rootScope);
		assertEquals(rootScope.getChildren().get(0), childScope);
		assertFalse(release.getScopeList().contains(insertedParent));
	}
}
