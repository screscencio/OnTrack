package br.com.oncast.ontrack.shared.model.action;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbolsProvider;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;

public class ScopeInsertSiblingDownAction_ReleaseCreationTest {

	private static final String SCOPE_DESCRIPTION = "new description " + StringRepresentationSymbolsProvider.RELEASE_SYMBOL;

	private ProjectContext context;
	private Scope rootScope;
	private Release rootRelease;

	@Before
	public void setUp() {
		rootScope = ScopeTestUtils.getScope();
		rootRelease = ReleaseTestUtils.getRelease();
		context = ProjectTestUtils.createProjectContext(rootScope, rootRelease);
	}

	@Test
	public void shouldBindScopeToRelease() throws UnableToCompleteActionException {
		final Scope scope = rootScope.getChild(2);
		new ScopeInsertSiblingDownAction(scope.getId(), SCOPE_DESCRIPTION + "R1").execute(context);

		assertTrue(rootRelease.getChild(0).getScopeList().contains(rootScope.getChild(3)));
	}

	@Test
	public void shouldCreateNewReleaseIfItDoesNotExist() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final String releaseDescription = "R4";
		assertThatReleaseIsNotInContext(releaseDescription);

		final Scope scope = rootScope.getChild(2);
		new ScopeInsertSiblingDownAction(scope.getId(), SCOPE_DESCRIPTION + releaseDescription).execute(context);

		final Release newRelease = assertThatReleaseIsInContext(releaseDescription);
		assertTrue(newRelease.getScopeList().contains(rootScope.getChild(3)));
	}

	@Test
	public void shouldNotCreateNewReleaseIfItAlreadyExist() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release release = rootRelease.getChild(0);
		assertThatReleaseIsInContext(release.getDescription());

		final Scope scope = rootScope.getChild(2);
		new ScopeInsertSiblingDownAction(scope.getId(), SCOPE_DESCRIPTION + release.getDescription()).execute(context);

		final Release loadedRelease = assertThatReleaseIsInContext(release.getDescription());
		assertTrue(loadedRelease.getScopeList().contains(rootScope.getChild(3)));
		assertEquals(release, loadedRelease);
	}

	@Test
	public void rollbackShouldDeletePreviouslyCreatedRelease() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final String releaseDescription = "R4";
		assertThatReleaseIsNotInContext(releaseDescription);

		final Scope scope = rootScope.getChild(2);
		final ModelAction rollbackAction = new ScopeInsertSiblingDownAction(scope.getId(), SCOPE_DESCRIPTION + releaseDescription).execute(context);

		final Release newRelease = assertThatReleaseIsInContext(releaseDescription);
		assertTrue(newRelease.getScopeList().contains(rootScope.getChild(3)));

		rollbackAction.execute(context);
		assertThatReleaseIsNotInContext(releaseDescription);
	}

	@Test
	public void rollbackShouldNotDeleteBoundReleaseIfThePreviouslyActionsDidNotCreatedIt() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final String releaseDescription = "R1";
		assertThatReleaseIsInContext(releaseDescription);

		final Scope scope = rootScope.getChild(2);
		final ModelAction rollbackAction = new ScopeInsertSiblingDownAction(scope.getId(), SCOPE_DESCRIPTION + releaseDescription).execute(context);

		final Release newRelease = assertThatReleaseIsInContext(releaseDescription);
		assertTrue(newRelease.getScopeList().contains(rootScope.getChild(3)));

		rollbackAction.execute(context);

		assertThatReleaseIsInContext(releaseDescription);
	}

	@Test
	public void shouldDeletePreviouslyCreatedReleaseAndRecreateItAfterMultiplesUndoAndRedo() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final String releaseDescription = "R4";
		assertThatReleaseIsNotInContext(releaseDescription);

		final Scope scope = rootScope.getChild(2);

		final ActionExecutionManager actionExecutionManager = new ActionExecutionManager(Mockito.mock(ActionExecutionListener.class));
		actionExecutionManager.doUserAction(new ScopeInsertSiblingDownAction(scope.getId(), SCOPE_DESCRIPTION + releaseDescription), context);

		Release newRelease = assertThatReleaseIsInContext(releaseDescription);
		assertTrue(newRelease.getScopeList().contains(rootScope.getChild(3)));

		for (int i = 0; i < 20; i++) {
			actionExecutionManager.undoUserAction(context);
			assertThatReleaseIsNotInContext(releaseDescription);

			actionExecutionManager.redoUserAction(context);
			newRelease = assertThatReleaseIsInContext(releaseDescription);
			assertTrue(newRelease.getScopeList().contains(rootScope.getChild(3)));
		}
	}

	private Release assertThatReleaseIsInContext(final String releaseDescription) throws ReleaseNotFoundException {
		final Release newRelease = context.findRelease(releaseDescription);
		return newRelease;
	}

	private void assertThatReleaseIsNotInContext(final String releaseDescription) {
		try {
			context.findRelease(releaseDescription);
			fail("The release should not exist in project context.");
		}
		catch (final ReleaseNotFoundException e) {}
	}

}