package br.com.oncast.ontrack.shared.model.actions;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.mocks.models.ReleaseTestUtils;
import br.com.oncast.ontrack.mocks.models.ScopeTestUtils;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class ReleaseRemoveActionTest {

	private ProjectContext context;
	private Scope rootScope;
	private Release rootRelease;

	@Before
	public void setUp() {
		rootScope = ScopeTestUtils.getScope();
		rootRelease = ReleaseTestUtils.getRelease();
		context = new ProjectContext(new Project(rootScope, rootRelease));

		assertEquals(3, rootRelease.getChildren().size());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void rootReleaseCantBeRemoved() throws UnableToCompleteActionException {
		new ReleaseRemoveAction(rootRelease.getId()).execute(context);
	}

	@Test
	public void theRemovedReleaseShouldBeRemovedFromItsParent() throws UnableToCompleteActionException {
		final Release removedRelease = rootRelease.getChild(2);
		new ReleaseRemoveAction(removedRelease.getId()).execute(context);

		assertEquals(2, rootRelease.getChildren().size());
		assertFalse(rootRelease.getChildren().contains(removedRelease));
	}

	@Test
	public void theRemovedReleaseShouldBeRemovedFromItsParent2() throws UnableToCompleteActionException {
		final Release removedRelease = rootRelease.getChild(0).getChild(0);
		new ReleaseRemoveAction(removedRelease.getId()).execute(context);

		assertEquals(2, rootRelease.getChild(0).getChildren().size());
		assertFalse(rootRelease.getChild(0).getChildren().contains(removedRelease));
	}

	@Test
	public void shouldRemoveChildrenReleasesWhenRemovingARelease() throws UnableToCompleteActionException {
		final Release removedRelease = rootRelease.getChild(0);
		final List<Release> removedChildrenReleases = removedRelease.getChildren();

		new ReleaseRemoveAction(removedRelease.getId()).execute(context);

		assertEquals(2, rootRelease.getChildren().size());
		assertFalse(rootRelease.getChildren().contains(removedRelease));
		assertTrue(removedRelease.getChildren().isEmpty());
		for (final Release release : removedChildrenReleases) {
			assertNull(release.getParent());
			assertFalse(rootRelease.getChild(0).getChildren().contains(release));
		}
	}

	@Test(expected = ReleaseNotFoundException.class)
	public void removedReleaseShouldNotBeInProjectContextAnymore() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release removedRelease = rootRelease.getChild(0);
		new ReleaseRemoveAction(removedRelease.getId()).execute(context);

		context.findRelease(removedRelease.getId());
	}

	@Test
	public void allDescendantsOfRemovedReleaseShouldNotBeInProjectContextAnymore() throws UnableToCompleteActionException {
		final Release removedRelease = rootRelease.getChild(0);
		final List<Release> removedChildrenReleases = removedRelease.getChildren();
		new ReleaseRemoveAction(removedRelease.getId()).execute(context);

		for (final Release release : removedChildrenReleases) {
			try {
				context.findRelease(release.getId());
				fail();
			}
			catch (final ReleaseNotFoundException e) {}
		}
	}

	@Test
	public void shouldDissociateChildrenScopesWhenRemovingARelease() throws UnableToCompleteActionException {
		final Release removedRelease = rootRelease.getChild(2);
		final Scope scope1 = rootScope.getChild(1);
		final Scope scope2 = rootScope.getChild(2);

		removedRelease.addScope(scope1);
		removedRelease.addScope(scope2);

		assertEquals(2, removedRelease.getScopeList().size());
		assertEquals(removedRelease, scope1.getRelease());
		assertEquals(removedRelease, scope2.getRelease());
		assertTrue(removedRelease.getScopeList().contains(scope1));
		assertTrue(removedRelease.getScopeList().contains(scope2));

		new ReleaseRemoveAction(removedRelease.getId()).execute(context);

		assertTrue(removedRelease.getScopeList().isEmpty());
		assertFalse(removedRelease.getScopeList().contains(scope1));
		assertFalse(removedRelease.getScopeList().contains(scope2));
		assertNull(scope1.getRelease());
		assertNull(scope2.getRelease());
	}

	@Test
	public void shouldDissociateChildrenScopesOfChildrenReleasesWhenRemovingARelease() throws UnableToCompleteActionException {
		final Release removedRelease = rootRelease.getChild(0);
		final Scope scope1 = rootScope.getChild(1);
		final Scope scope2 = rootScope.getChild(2);
		removedRelease.addScope(scope1);
		removedRelease.addScope(scope2);

		final Release childRelease = rootRelease.getChild(0).getChild(0);
		final Scope scope3 = rootScope.getChild(0).getChild(0);
		final Scope scope4 = rootScope.getChild(0).getChild(1);
		childRelease.addScope(scope3);
		childRelease.addScope(scope4);

		assertEquals(2, removedRelease.getScopeList().size());
		assertEquals(removedRelease, scope1.getRelease());
		assertEquals(removedRelease, scope2.getRelease());
		assertTrue(removedRelease.getScopeList().contains(scope1));
		assertTrue(removedRelease.getScopeList().contains(scope2));

		assertEquals(2, childRelease.getScopeList().size());
		assertEquals(childRelease, scope3.getRelease());
		assertEquals(childRelease, scope4.getRelease());
		assertTrue(childRelease.getScopeList().contains(scope3));
		assertTrue(childRelease.getScopeList().contains(scope4));

		new ReleaseRemoveAction(removedRelease.getId()).execute(context);

		assertTrue(removedRelease.getScopeList().isEmpty());
		assertFalse(removedRelease.getScopeList().contains(scope1));
		assertFalse(removedRelease.getScopeList().contains(scope2));
		assertNull(scope1.getRelease());
		assertNull(scope2.getRelease());

		assertTrue(childRelease.getScopeList().isEmpty());
		assertFalse(childRelease.getScopeList().contains(scope1));
		assertFalse(childRelease.getScopeList().contains(scope2));
		assertNull(scope1.getRelease());
		assertNull(scope2.getRelease());
	}

	@Test
	public void rollbackShouldReinsertRemovedReleaseIntoItsParent() throws UnableToCompleteActionException {
		final Release removedRelease = rootRelease.getChild(2);
		final ModelAction rollbackAction = new ReleaseRemoveAction(removedRelease.getId()).execute(context);

		assertEquals(2, rootRelease.getChildren().size());
		assertFalse(rootRelease.getChildren().contains(removedRelease));

		rollbackAction.execute(context);

		assertEquals(3, rootRelease.getChildren().size());
		assertTrue(rootRelease.getChildren().contains(removedRelease));
	}

	@Test
	public void rollbackShouldReinsertRemovedReleaseInTheSamePositionItWasBeforeRemoval() throws UnableToCompleteActionException {
		final int position = 0;
		final Release removedRelease = rootRelease.getChild(position);
		final ModelAction rollbackAction = new ReleaseRemoveAction(removedRelease.getId()).execute(context);

		assertEquals(2, rootRelease.getChildren().size());
		assertFalse(rootRelease.getChildren().contains(removedRelease));
		assertFalse(rootRelease.getChild(0).getDescription().equals(removedRelease.getDescription()));

		rollbackAction.execute(context);

		assertEquals(3, rootRelease.getChildren().size());
		assertEquals(position, rootRelease.getChildren().indexOf(removedRelease));
	}

	@Test
	public void rollbackShouldRecreateEntireReleaseHierarchyInTheSameOrder() throws UnableToCompleteActionException {
		final Release removedRelease = rootRelease.getChild(0);
		final Release childRelease0 = removedRelease.getChild(0);
		final Release childRelease1 = removedRelease.getChild(1);
		final Release childRelease2 = removedRelease.getChild(2);

		final ModelAction rollbackAction = new ReleaseRemoveAction(removedRelease.getId()).execute(context);

		assertEquals(2, rootRelease.getChildren().size());
		assertFalse(rootRelease.getChildren().contains(removedRelease));

		rollbackAction.execute(context);

		assertEquals(3, rootRelease.getChildren().size());
		assertEquals(0, rootRelease.getChildren().indexOf(removedRelease));
		assertEquals(0, rootRelease.getChild(0).getChildren().indexOf(childRelease0));
		assertEquals(1, rootRelease.getChild(0).getChildren().indexOf(childRelease1));
		assertEquals(2, rootRelease.getChild(0).getChildren().indexOf(childRelease2));
	}

	@Test
	public void removedReleaseShouldBeInProjectContextAfterRollback() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release removedRelease = rootRelease.getChild(2);
		final ModelAction rollbackAction = new ReleaseRemoveAction(removedRelease.getId()).execute(context);

		try {
			context.findRelease(removedRelease.getId());
			fail();
		}
		catch (final ReleaseNotFoundException e) {}

		rollbackAction.execute(context);

		assertEquals(removedRelease, context.findRelease(removedRelease.getId()));
	}

	@Test
	public void allDescendantsOfRemovedReleaseShouldBeInProjectContextAfterRollback() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release removedRelease = rootRelease.getChild(2);
		final ModelAction rollbackAction = new ReleaseRemoveAction(removedRelease.getId()).execute(context);

		for (final Release childRelease : removedRelease.getChildren()) {
			try {
				context.findRelease(childRelease.getId());
				fail();
			}
			catch (final ReleaseNotFoundException e) {}
		}

		rollbackAction.execute(context);

		for (final Release childRelease : removedRelease.getChildren())
			assertEquals(childRelease, context.findRelease(childRelease.getId()));
	}

	@Test
	public void rollbackShouldReassociateScopesToRemovedRelease() throws UnableToCompleteActionException, ReleaseNotFoundException {
		Release removedRelease = rootRelease.getChild(2);
		final Scope scope1 = rootScope.getChild(1);
		final Scope scope2 = rootScope.getChild(2);

		removedRelease.addScope(scope1);
		removedRelease.addScope(scope2);

		assertEquals(2, removedRelease.getScopeList().size());
		assertEquals(removedRelease, scope1.getRelease());
		assertEquals(removedRelease, scope2.getRelease());
		assertTrue(removedRelease.getScopeList().contains(scope1));
		assertTrue(removedRelease.getScopeList().contains(scope2));

		final ReleaseRemoveRollbackAction rollbackAction = new ReleaseRemoveAction(removedRelease.getId()).execute(context);

		assertTrue(removedRelease.getScopeList().isEmpty());
		assertFalse(removedRelease.getScopeList().contains(scope1));
		assertFalse(removedRelease.getScopeList().contains(scope2));
		assertNull(scope1.getRelease());
		assertNull(scope2.getRelease());

		rollbackAction.execute(context);

		removedRelease = context.findRelease(removedRelease.getId());
		assertEquals(2, removedRelease.getScopeList().size());
		assertEquals(removedRelease, scope1.getRelease());
		assertEquals(removedRelease, scope2.getRelease());
		assertTrue(removedRelease.getScopeList().contains(scope1));
		assertTrue(removedRelease.getScopeList().contains(scope2));
	}

	@Test
	public void shouldHandleRemovalCorrectlyAfterMultipleUndosAndRedos() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release removedRelease = rootRelease.getChild(0);
		final Release childRelease0 = removedRelease.getChild(0);
		final Release childRelease1 = removedRelease.getChild(1);
		final Release childRelease2 = removedRelease.getChild(2);

		final ActionExecutionManager actionExecutionManager = new ActionExecutionManager(Mockito.mock(ActionExecutionListener.class));
		actionExecutionManager.doUserAction(new ReleaseRemoveAction(removedRelease.getId()), context);

		try {
			context.findRelease(removedRelease.getId());
			fail();
		}
		catch (final ReleaseNotFoundException e) {
			// Removed release should not be in context anymore. This exception is expected. If it is not thrown, the test fails.
		}

		for (int i = 0; i < 20; i++) {
			actionExecutionManager.undoUserAction(context);

			assertEquals(3, rootRelease.getChildren().size());
			assertEquals(0, rootRelease.getChildren().indexOf(removedRelease));
			assertEquals(0, rootRelease.getChild(0).getChildren().indexOf(childRelease0));
			assertEquals(1, rootRelease.getChild(0).getChildren().indexOf(childRelease1));
			assertEquals(2, rootRelease.getChild(0).getChildren().indexOf(childRelease2));

			assertEquals(removedRelease, context.findRelease(removedRelease.getId()));
			assertEquals(childRelease0, context.findRelease(childRelease0.getId()));
			assertEquals(childRelease1, context.findRelease(childRelease1.getId()));
			assertEquals(childRelease2, context.findRelease(childRelease2.getId()));

			actionExecutionManager.redoUserAction(context);

			assertEquals(2, rootRelease.getChildren().size());
			assertFalse(rootRelease.getChildren().contains(removedRelease));
			assertFalse(rootRelease.getChildren().contains(removedRelease));

		}
	}
}
