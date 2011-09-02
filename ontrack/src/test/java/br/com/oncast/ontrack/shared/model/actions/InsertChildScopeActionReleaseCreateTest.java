package br.com.oncast.ontrack.shared.model.actions;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.mocks.models.ReleaseMock;
import br.com.oncast.ontrack.mocks.models.ScopeMock;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbolsProvider;

public class InsertChildScopeActionReleaseCreateTest {

	private static final String SCOPE_DESCRIPTION = "new description " + StringRepresentationSymbolsProvider.RELEASE_SYMBOL;

	private ProjectContext context;
	private Scope rootScope;
	private Release rootRelease;

	@Before
	public void setUp() {
		rootScope = ScopeMock.getScope();
		rootRelease = ReleaseMock.getRelease();
		context = new ProjectContext(new Project(rootScope, rootRelease));
	}

	@Test
	public void shouldBindScopeToRelease() throws UnableToCompleteActionException {
		final Scope scope = rootScope.getChild(1);
		new ScopeInsertChildAction(scope.getId(), SCOPE_DESCRIPTION + "R1").execute(context);

		assertTrue(rootRelease.getChild(0).getScopeList().contains(scope.getChild(0)));
	}

	@Test
	public void shouldCreateNewReleaseIfItDoesNotExist() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final String releaseDescription = "R4";
		assertThatReleaseIsNotInContext(releaseDescription);

		final Scope scope = rootScope.getChild(1);
		new ScopeInsertChildAction(scope.getId(), SCOPE_DESCRIPTION + releaseDescription).execute(context);

		final Release newRelease = assertThatReleaseIsInContext(releaseDescription);
		assertTrue(newRelease.getScopeList().contains(scope.getChild(0)));
	}

	@Test
	public void shouldNotCreateNewReleaseIfItAlreadyExist() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release release = rootRelease.getChild(0);
		assertThatReleaseIsInContext(release.getDescription());

		final Scope scope = rootScope.getChild(1);
		new ScopeInsertChildAction(scope.getId(), SCOPE_DESCRIPTION + release.getDescription()).execute(context);

		final Release loadedRelease = assertThatReleaseIsInContext(release.getDescription());
		assertTrue(loadedRelease.getScopeList().contains(scope.getChild(0)));
		assertEquals(release, loadedRelease);
	}

	@Test
	public void rollbackShouldDeletePreviouslyCreatedRelease() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final String releaseDescription = "R4";
		assertThatReleaseIsNotInContext(releaseDescription);

		final Scope scope = rootScope.getChild(1);
		final ModelAction rollbackAction = new ScopeInsertChildAction(scope.getId(), SCOPE_DESCRIPTION + releaseDescription).execute(context);

		final Release newRelease = assertThatReleaseIsInContext(releaseDescription);
		assertTrue(newRelease.getScopeList().contains(scope.getChild(0)));

		rollbackAction.execute(context);
		assertThatReleaseIsNotInContext(releaseDescription);
	}

	@Test
	public void rollbackShouldNotDeleteBoundReleaseIfThePreviouslyActionsDidNotCreatedIt() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final String releaseDescription = "R1";
		assertThatReleaseIsInContext(releaseDescription);

		final Scope scope = rootScope.getChild(1);
		final ModelAction rollbackAction = new ScopeInsertChildAction(scope.getId(), SCOPE_DESCRIPTION + releaseDescription).execute(context);

		final Release newRelease = assertThatReleaseIsInContext(releaseDescription);
		assertTrue(newRelease.getScopeList().contains(scope.getChild(0)));

		rollbackAction.execute(context);

		assertThatReleaseIsInContext(releaseDescription);
	}

	@Test
	public void shouldDeletePreviouslyCreatedReleaseAndRecreateItAfterMultiplesUndoAndRedo() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final String releaseDescription = "R4";
		assertThatReleaseIsNotInContext(releaseDescription);

		final Scope scope = rootScope.getChild(1);

		final ActionExecutionManager actionExecutionManager = new ActionExecutionManager(Mockito.mock(ActionExecutionListener.class));
		actionExecutionManager.doUserAction(new ScopeInsertChildAction(scope.getId(), SCOPE_DESCRIPTION + releaseDescription), context);

		Release newRelease = assertThatReleaseIsInContext(releaseDescription);
		assertTrue(newRelease.getScopeList().contains(scope.getChild(0)));

		for (int i = 0; i < 20; i++) {
			actionExecutionManager.undoUserAction(context);
			assertThatReleaseIsNotInContext(releaseDescription);

			actionExecutionManager.redoUserAction(context);
			newRelease = assertThatReleaseIsInContext(releaseDescription);
			assertTrue(newRelease.getScopeList().contains(scope.getChild(0)));
		}
	}

	private Release assertThatReleaseIsInContext(final String releaseDescription) throws ReleaseNotFoundException {
		final Release newRelease = context.loadRelease(releaseDescription);
		return newRelease;
	}

	private void assertThatReleaseIsNotInContext(final String releaseDescription) {
		try {
			context.loadRelease(releaseDescription);
			fail("The release should not exist in project context.");
		}
		catch (final ReleaseNotFoundException e) {}
	}

}
