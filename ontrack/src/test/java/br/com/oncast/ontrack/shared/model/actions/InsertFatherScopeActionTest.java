package br.com.oncast.ontrack.shared.model.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseMockFactory;
import br.com.oncast.ontrack.shared.model.scope.Scope;
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

		context = new ProjectContext(new Project(rootScope, ReleaseMockFactory.create("")));
	}

	@Test
	public void mustInsertAScopeAsFather() throws UnableToCompleteActionException {
		assertEquals(childScope.getParent(), rootScope);
		assertEquals(rootScope.getChildren().get(0), childScope);

		new ScopeInsertParentAction(childScope.getId(), newScopeDescription).execute(context);

		assertEquals(childScope.getParent().getDescription(), newScopeDescription);
		assertEquals(rootScope.getChildren().get(0).getDescription(), newScopeDescription);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void insertingFatherAtRootNodeMustThrowException() throws UnableToCompleteActionException {
		new ScopeInsertParentAction(rootScope.getId(), newScopeDescription).execute(context);
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		final ScopeInsertParentAction insertFatherScopeAction = new ScopeInsertParentAction(childScope.getId(), newScopeDescription);
		final ScopeAction rollbackAction = insertFatherScopeAction.execute(context);

		assertEquals(childScope.getParent().getDescription(), newScopeDescription);
		assertEquals(rootScope.getChildren().get(0).getDescription(), newScopeDescription);

		rollbackAction.execute(context);

		assertEquals(childScope.getParent(), rootScope);
		assertEquals(rootScope.getChildren().get(0), childScope);
	}

	@Test
	public void mustAssociateScopeWithARelease() throws UnableToCompleteActionException {
		new ScopeInsertParentAction(childScope.getId(), newScopeDescription + " @" + newReleaseDescription).execute(context);

		assertEquals(childScope.getParent().getDescription(), newScopeDescription);
		assertEquals(childScope.getParent().getRelease().getDescription(), newReleaseDescription);
		assertEquals(rootScope.getChildren().get(0).getDescription(), newScopeDescription);
	}

	@Test
	public void mustDisassociateScopeFromReleaseAfterUndo() throws UnableToCompleteActionException {
		final ScopeInsertParentAction insertFatherScopeAction = new ScopeInsertParentAction(childScope.getId(), newScopeDescription + " @"
				+ newReleaseDescription);
		final ScopeAction rollbackAction = insertFatherScopeAction.execute(context);

		final Scope insertedParent = childScope.getParent();
		final Release release = insertedParent.getRelease();

		assertEquals(release.getDescription(), newReleaseDescription);
		assertTrue(release.getScopeList().contains(insertedParent));
		assertEquals(insertedParent.getDescription(), newScopeDescription);
		assertEquals(rootScope.getChildren().get(0).getDescription(), newScopeDescription);

		rollbackAction.execute(context);

		assertEquals(childScope.getParent(), rootScope);
		assertEquals(rootScope.getChildren().get(0), childScope);
		assertFalse(release.getScopeList().contains(insertedParent));
	}
}
