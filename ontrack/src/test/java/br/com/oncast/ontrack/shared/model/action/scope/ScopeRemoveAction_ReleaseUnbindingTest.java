package br.com.oncast.ontrack.shared.model.action.scope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveRollbackAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class ScopeRemoveAction_ReleaseUnbindingTest {

	private Scope rootScope;
	private ProjectContext context;
	private Release release;

	@Mock
	private ActionContext actionContext;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(actionContext.getUserId()).thenReturn(UserTestUtils.getAdmin().getId());
		when(actionContext.getTimestamp()).thenReturn(new Date(Long.MAX_VALUE));

		rootScope = ScopeTestUtils.getScope();
		final Release rootRelease = ReleaseTestUtils.getRelease();
		release = rootRelease.getChildren().get(0);
		context = ProjectTestUtils.createProjectContext(rootScope, rootRelease);
	}

	@Test
	public void shouldRemoveScopeOfRelease() throws UnableToCompleteActionException {
		final Scope removedScope = rootScope.getChild(1);
		release.addScope(removedScope);
		removedScope.setRelease(release);

		new ScopeRemoveAction(removedScope.getId()).execute(context, actionContext);

		assertNull(removedScope.getRelease());
		assertFalse(release.getScopeList().contains(removedScope));
	}

	@Test
	public void rollbackShouldGiveBackReleaseAndScopeAssociation() throws UnableToCompleteActionException {
		final Scope removedScope = rootScope.getChild(1);
		addScopeToRelease(removedScope, release);

		final ScopeRemoveRollbackAction rollbackAction = new ScopeRemoveAction(removedScope.getId()).execute(context, actionContext);
		assertNull(removedScope.getRelease());
		assertFalse(release.getScopeList().contains(removedScope));

		rollbackAction.execute(context, actionContext);
		assertEquals(release, rootScope.getChild(1).getRelease());
		assertTrue(release.getScopeList().contains(rootScope.getChild(1)));
	}

	@Test
	public void shouldRemoveReleaseFromChildrenScopesWhenParentIsRemoved() throws UnableToCompleteActionException {
		final Scope removedScope = rootScope.getChild(0);
		final Scope removedScope1 = rootScope.getChild(0).getChild(0).getChild(0);
		final Scope removedScope2 = rootScope.getChild(0).getChild(0).getChild(1);
		final Scope removedScope3 = rootScope.getChild(0).getChild(1);
		addScopeToRelease(removedScope1, release);
		addScopeToRelease(removedScope2, release);
		addScopeToRelease(removedScope3, release);

		new ScopeRemoveAction(removedScope.getId()).execute(context, actionContext);
		assertNull(removedScope1.getRelease());
		assertFalse(release.getScopeList().contains(removedScope1));

		assertNull(removedScope2.getRelease());
		assertFalse(release.getScopeList().contains(removedScope2));

		assertNull(removedScope3.getRelease());
		assertFalse(release.getScopeList().contains(removedScope3));
	}

	@Test
	public void shouldGiveBackReleaseToChildrenScopesWhenParentIsRemovedAndRollbackIsExecuted() throws UnableToCompleteActionException {
		final Scope removedScope = rootScope.getChild(0);
		final Scope removedScope1 = rootScope.getChild(0).getChild(0).getChild(0);
		final Scope removedScope2 = rootScope.getChild(0).getChild(0).getChild(1);
		final Scope removedScope3 = rootScope.getChild(0).getChild(1);
		addScopeToRelease(removedScope1, release);
		addScopeToRelease(removedScope2, release);
		addScopeToRelease(removedScope3, release);

		final ScopeRemoveRollbackAction rollbackAction = new ScopeRemoveAction(removedScope.getId()).execute(context, actionContext);
		assertNull(removedScope1.getRelease());
		assertFalse(release.getScopeList().contains(removedScope1));
		assertNull(removedScope2.getRelease());
		assertFalse(release.getScopeList().contains(removedScope2));
		assertNull(removedScope3.getRelease());
		assertFalse(release.getScopeList().contains(removedScope3));

		rollbackAction.execute(context, actionContext);

		assertEquals(release, rootScope.getChild(0).getChild(0).getChild(0).getRelease());
		assertTrue(release.getScopeList().contains(removedScope1));
		assertEquals(release, rootScope.getChild(0).getChild(0).getChild(1).getRelease());
		assertTrue(release.getScopeList().contains(removedScope2));
		assertEquals(release, rootScope.getChild(0).getChild(1).getRelease());
		assertTrue(release.getScopeList().contains(removedScope3));
	}

	@Test
	public void shouldHandleReleaseDisassociationCorrectlyAfterMultipleUndosAndRedos() throws UnableToCompleteActionException {
		final Scope removedScope = rootScope.getChild(0);
		Scope removedScope1 = rootScope.getChild(0).getChild(0).getChild(0);
		Scope removedScope2 = rootScope.getChild(0).getChild(0).getChild(1);
		Scope removedScope3 = rootScope.getChild(0).getChild(1);
		addScopeToRelease(removedScope1, release);
		addScopeToRelease(removedScope2, release);
		addScopeToRelease(removedScope3, release);

		final ActionExecutionManager actionExecutionManager = new ActionExecutionManager(Mockito.mock(ActionExecutionListener.class));
		actionExecutionManager.doUserAction(new ScopeRemoveAction(removedScope.getId()), context, actionContext);

		for (int i = 0; i < 20; i++) {
			assertNull(removedScope1.getRelease());
			assertFalse(release.getScopeList().contains(removedScope1));
			assertNull(removedScope2.getRelease());
			assertFalse(release.getScopeList().contains(removedScope2));
			assertNull(removedScope3.getRelease());
			assertFalse(release.getScopeList().contains(removedScope3));

			actionExecutionManager.undoUserAction(context, actionContext);

			removedScope1 = rootScope.getChild(0).getChild(0).getChild(0);
			removedScope2 = rootScope.getChild(0).getChild(0).getChild(1);
			removedScope3 = rootScope.getChild(0).getChild(1);

			assertEquals(release, removedScope1.getRelease());
			assertTrue(release.getScopeList().contains(removedScope1));
			assertEquals(release, removedScope2.getRelease());
			assertTrue(release.getScopeList().contains(removedScope2));
			assertEquals(release, removedScope3.getRelease());
			assertTrue(release.getScopeList().contains(removedScope3));

			actionExecutionManager.redoUserAction(context, actionContext);

			assertNull(removedScope1.getRelease());
			assertFalse(release.getScopeList().contains(removedScope1));
			assertNull(removedScope2.getRelease());
			assertFalse(release.getScopeList().contains(removedScope2));
			assertNull(removedScope3.getRelease());
			assertFalse(release.getScopeList().contains(removedScope3));
		}
	}

	private void addScopeToRelease(final Scope scope, final Release releaseToBeAdded) {
		releaseToBeAdded.addScope(scope);
		scope.setRelease(releaseToBeAdded);
	}
}
