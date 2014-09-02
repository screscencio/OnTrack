package br.com.oncast.ontrack.shared.model.action.scope;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.server.services.exportImport.xml.UserActionTestUtils;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingUpAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbolsProvider;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import static org.mockito.Mockito.when;

import static org.junit.Assert.assertTrue;

public class ScopeInsertSiblingUpAction_ReleaseCreationTest {

	private static final String SCOPE_DESCRIPTION = "new description " + StringRepresentationSymbolsProvider.RELEASE_SYMBOL;

	private ProjectContext context;
	private Scope rootScope;
	private Release rootRelease;

	@Mock
	private ActionContext actionContext;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(actionContext.getUserId()).thenReturn(UserTestUtils.getAdmin().getId());
		when(actionContext.getTimestamp()).thenReturn(new Date(0));

		rootScope = ScopeTestUtils.getScope();
		rootRelease = ReleaseTestUtils.getRelease();
		context = ProjectTestUtils.createProjectContext(rootScope, rootRelease);
	}

	@Test
	public void shouldBindScopeToRelease() throws UnableToCompleteActionException {
		final Scope scope = rootScope.getChild(2);
		new ScopeInsertSiblingUpAction(scope.getId(), SCOPE_DESCRIPTION + "R1").execute(context, actionContext);

		assertTrue(rootRelease.getChild(0).getScopeList().contains(rootScope.getChild(2)));
	}

	@Test
	public void shouldCreateNewReleaseIfItDoesNotExist() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final String releaseDescription = "R4";
		assertThatReleaseIsNotInContext(releaseDescription);

		final Scope scope = rootScope.getChild(2);
		new ScopeInsertSiblingUpAction(scope.getId(), SCOPE_DESCRIPTION + releaseDescription).execute(context, actionContext);

		final Release newRelease = assertThatReleaseIsInContext(releaseDescription);
		assertTrue(newRelease.getScopeList().contains(rootScope.getChild(2)));
	}

	@Test
	public void shouldNotCreateNewReleaseIfItAlreadyExist() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release release = rootRelease.getChild(0);
		assertThatReleaseIsInContext(release.getDescription());

		final Scope scope = rootScope.getChild(2);
		new ScopeInsertSiblingUpAction(scope.getId(), SCOPE_DESCRIPTION + release.getDescription()).execute(context, actionContext);

		final Release loadedRelease = assertThatReleaseIsInContext(release.getDescription());
		assertTrue(loadedRelease.getScopeList().contains(rootScope.getChild(2)));
		assertEquals(release, loadedRelease);
	}

	@Test
	public void rollbackShouldDeletePreviouslyCreatedRelease() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final String releaseDescription = "R4";
		assertThatReleaseIsNotInContext(releaseDescription);

		final Scope scope = rootScope.getChild(2);
		final ModelAction rollbackAction = new ScopeInsertSiblingUpAction(scope.getId(), SCOPE_DESCRIPTION + releaseDescription).execute(context, actionContext);

		final Release newRelease = assertThatReleaseIsInContext(releaseDescription);
		assertTrue(newRelease.getScopeList().contains(rootScope.getChild(2)));

		rollbackAction.execute(context, actionContext);
		assertThatReleaseIsNotInContext(releaseDescription);
	}

	@Test
	public void rollbackShouldNotDeleteBoundReleaseIfThePreviouslyActionsDidNotCreatedIt() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final String releaseDescription = "R1";
		assertThatReleaseIsInContext(releaseDescription);

		final Scope scope = rootScope.getChild(2);
		final ModelAction rollbackAction = new ScopeInsertSiblingUpAction(scope.getId(), SCOPE_DESCRIPTION + releaseDescription).execute(context, actionContext);

		final Release newRelease = assertThatReleaseIsInContext(releaseDescription);
		assertTrue(newRelease.getScopeList().contains(rootScope.getChild(2)));

		rollbackAction.execute(context, actionContext);

		assertThatReleaseIsInContext(releaseDescription);
	}

	@Test
	public void shouldDeletePreviouslyCreatedReleaseAndRecreateItAfterMultiplesUndoAndRedo() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final String releaseDescription = "R4";
		assertThatReleaseIsNotInContext(releaseDescription);

		final Scope scope = rootScope.getChild(2);

		final ActionExecutionManager actionExecutionManager = ActionExecutionTestUtils.createManager(context);
		actionExecutionManager.doUserAction(UserActionTestUtils.create(new ScopeInsertSiblingUpAction(scope.getId(), SCOPE_DESCRIPTION + releaseDescription), actionContext));

		Release newRelease = assertThatReleaseIsInContext(releaseDescription);
		assertTrue(newRelease.getScopeList().contains(rootScope.getChild(2)));

		for (int i = 0; i < 20; i++) {
			actionExecutionManager.undoUserAction();
			assertThatReleaseIsNotInContext(releaseDescription);

			actionExecutionManager.redoUserAction();
			newRelease = assertThatReleaseIsInContext(releaseDescription);
			assertTrue(newRelease.getScopeList().contains(rootScope.getChild(2)));
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
		} catch (final ReleaseNotFoundException e) {}
	}

}
