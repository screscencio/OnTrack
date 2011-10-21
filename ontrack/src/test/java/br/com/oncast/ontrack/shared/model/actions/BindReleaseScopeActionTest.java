package br.com.oncast.ontrack.shared.model.actions;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.mocks.models.ReleaseTestUtils;
import br.com.oncast.ontrack.mocks.models.ScopeTestUtils;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class BindReleaseScopeActionTest {

	private ProjectContext context;
	private Scope rootScope;
	private Release rootRelease;

	@Before
	public void setUp() {
		rootScope = ScopeTestUtils.getScope();
		rootRelease = ReleaseTestUtils.getRelease();
		context = new ProjectContext(new Project(rootScope, rootRelease));
	}

	@Test
	public void shouldBindScopeToRelease() throws UnableToCompleteActionException {
		final Scope scope = rootScope.getChild(0);
		assertFalse(rootRelease.getChild(0).getScopeList().contains(scope));

		new ScopeBindReleaseAction(scope.getId(), "R1").execute(context);
		assertTrue(rootRelease.getChild(0).getScopeList().contains(scope));
	}

	@Test
	public void shouldUnbindScopeFromRelease() throws UnableToCompleteActionException {
		final Scope scope = rootScope.getChild(0);
		final Release release = rootRelease.getChild(0);
		release.addScope(scope);
		assertTrue(rootRelease.getChild(0).getScopeList().contains(scope));

		new ScopeBindReleaseAction(scope.getId(), "").execute(context);
		assertFalse(rootRelease.getChild(0).getScopeList().contains(scope));
	}

	@Test
	public void shouldCreateNewReleaseIfItDoesNotExist() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final String releaseDescription = "R4";
		assertThatReleaseIsNotInContext(releaseDescription);

		final Scope scope = rootScope.getChild(0);
		new ScopeBindReleaseAction(scope.getId(), releaseDescription).execute(context);

		final Release newRelease = assertThatReleaseIsInContext(releaseDescription);
		assertTrue(newRelease.getScopeList().contains(scope));
	}

	@Test
	public void shouldNotCreateNewReleaseIfItAlreadyExist() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release release = rootRelease.getChild(0);
		assertThatReleaseIsInContext(release.getDescription());

		final Scope scope = rootScope.getChild(0);
		new ScopeBindReleaseAction(scope.getId(), release.getDescription()).execute(context);

		final Release loadedRelease = assertThatReleaseIsInContext(release.getDescription());
		assertTrue(loadedRelease.getScopeList().contains(scope));
		assertEquals(release, loadedRelease);
	}

	@Test
	public void rollbackShouldDeletePreviouslyCreatedRelease() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final String releaseDescription = "R4";
		assertThatReleaseIsNotInContext(releaseDescription);

		final Scope scope = rootScope.getChild(0);
		final ModelAction rollbackAction = new ScopeBindReleaseAction(scope.getId(), releaseDescription).execute(context);

		final Release newRelease = assertThatReleaseIsInContext(releaseDescription);
		assertTrue(newRelease.getScopeList().contains(scope));

		rollbackAction.execute(context);

		assertThatReleaseIsNotInContext(releaseDescription);
	}

	@Test
	public void rollbackShouldNotDeleteBoundReleaseIfThePreviouslyActionsDidNotCreatedIt() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final String releaseDescription = "R1";
		assertThatReleaseIsInContext(releaseDescription);

		final Scope scope = rootScope.getChild(0);
		final ModelAction rollbackAction = new ScopeBindReleaseAction(scope.getId(), releaseDescription).execute(context);

		final Release newRelease = assertThatReleaseIsInContext(releaseDescription);
		assertTrue(newRelease.getScopeList().contains(scope));

		rollbackAction.execute(context);

		assertThatReleaseIsInContext(releaseDescription);
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
