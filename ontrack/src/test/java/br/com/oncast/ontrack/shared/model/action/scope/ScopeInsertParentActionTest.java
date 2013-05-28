package br.com.oncast.ontrack.shared.model.action.scope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeInsertParentActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertParentAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

public class ScopeInsertParentActionTest extends ModelActionTest {

	private Scope rootScope;
	private Scope childScope;
	private ProjectContext context;
	private String newScopeDescription;
	private String newReleaseDescription;

	@Before
	public void setUp() {
		rootScope = ScopeTestUtils.createScope("root");
		childScope = ScopeTestUtils.createScope("child");
		rootScope.add(childScope);

		newScopeDescription = "description for new scope";
		newReleaseDescription = "Release1";

		context = ProjectTestUtils.createProjectContext(rootScope, ReleaseTestUtils.createRelease(""));
	}

	@Test
	public void mustInsertAScopeAsFather() throws UnableToCompleteActionException {
		assertEquals(childScope.getParent(), rootScope);
		assertEquals(rootScope.getChildren().get(0), childScope);

		new ScopeInsertParentAction(childScope.getId(), newScopeDescription).execute(context, actionContext);

		assertEquals(childScope.getParent().getDescription(), newScopeDescription);
		assertEquals(rootScope.getChildren().get(0).getDescription(), newScopeDescription);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void insertingFatherAtRootNodeMustThrowException() throws UnableToCompleteActionException {
		new ScopeInsertParentAction(rootScope.getId(), newScopeDescription).execute(context, actionContext);
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		final ScopeInsertParentAction insertFatherScopeAction = new ScopeInsertParentAction(childScope.getId(), newScopeDescription);
		final ScopeAction rollbackAction = insertFatherScopeAction.execute(context, actionContext);

		assertEquals(childScope.getParent().getDescription(), newScopeDescription);
		assertEquals(rootScope.getChildren().get(0).getDescription(), newScopeDescription);

		rollbackAction.execute(context, actionContext);

		assertEquals(childScope.getParent(), rootScope);
		assertEquals(rootScope.getChildren().get(0), childScope);
	}

	@Test
	public void mustAssociateScopeWithARelease() throws UnableToCompleteActionException {
		new ScopeInsertParentAction(childScope.getId(), newScopeDescription + " @" + newReleaseDescription).execute(context, actionContext);

		assertEquals(childScope.getParent().getDescription(), newScopeDescription);
		assertEquals(childScope.getParent().getRelease().getDescription(), newReleaseDescription);
		assertEquals(rootScope.getChildren().get(0).getDescription(), newScopeDescription);
	}

	@Test
	public void mustDisassociateScopeFromReleaseAfterUndo() throws UnableToCompleteActionException {
		final ScopeInsertParentAction insertFatherScopeAction = new ScopeInsertParentAction(childScope.getId(), newScopeDescription + " @"
				+ newReleaseDescription);
		final ScopeAction rollbackAction = insertFatherScopeAction.execute(context, actionContext);

		final Scope insertedParent = childScope.getParent();
		final Release release = insertedParent.getRelease();

		assertEquals(release.getDescription(), newReleaseDescription);
		assertTrue(release.getScopeList().contains(insertedParent));
		assertEquals(insertedParent.getDescription(), newScopeDescription);
		assertEquals(rootScope.getChildren().get(0).getDescription(), newScopeDescription);

		rollbackAction.execute(context, actionContext);

		assertEquals(childScope.getParent(), rootScope);
		assertEquals(rootScope.getChildren().get(0), childScope);
		assertFalse(release.getScopeList().contains(insertedParent));
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ScopeInsertParentActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ScopeInsertParentAction.class;
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ScopeInsertParentAction(new UUID(), "");
	}
}
