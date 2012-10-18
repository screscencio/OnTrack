package br.com.oncast.ontrack.shared.model.action.release;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseRemoveActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ReleaseRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRemoveRollbackAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.AnnotationTestUtils;
import br.com.oncast.ontrack.utils.ChecklistTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class ReleaseRemoveActionTest extends ModelActionTest {

	private ProjectContext context;
	private Scope rootScope;
	private Release rootRelease;

	@Before
	public void setUp() {
		rootScope = ScopeTestUtils.getScope();
		rootRelease = ReleaseTestUtils.getRelease();
		context = ProjectTestUtils.createProjectContext(rootScope, rootRelease);

		assertEquals(3, rootRelease.getChildren().size());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void rootReleaseCantBeRemoved() throws UnableToCompleteActionException {
		new ReleaseRemoveAction(rootRelease.getId()).execute(context, actionContext);
	}

	@Test
	public void theRemovedReleaseShouldBeRemovedFromItsParent() throws UnableToCompleteActionException {
		final Release removedRelease = rootRelease.getChild(2);
		new ReleaseRemoveAction(removedRelease.getId()).execute(context, actionContext);

		assertEquals(2, rootRelease.getChildren().size());
		assertFalse(rootRelease.getChildren().contains(removedRelease));
	}

	@Test
	public void theRemovedReleaseShouldBeRemovedFromItsParent2() throws UnableToCompleteActionException {
		final Release removedRelease = rootRelease.getChild(0).getChild(0);
		new ReleaseRemoveAction(removedRelease.getId()).execute(context, actionContext);

		assertEquals(2, rootRelease.getChild(0).getChildren().size());
		assertFalse(rootRelease.getChild(0).getChildren().contains(removedRelease));
	}

	@Test
	public void shouldRemoveChildrenReleasesWhenRemovingARelease() throws UnableToCompleteActionException {
		final Release removedRelease = rootRelease.getChild(0);
		final List<Release> removedChildrenReleases = removedRelease.getChildren();

		new ReleaseRemoveAction(removedRelease.getId()).execute(context, actionContext);

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
		new ReleaseRemoveAction(removedRelease.getId()).execute(context, actionContext);

		context.findRelease(removedRelease.getId());
	}

	@Test
	public void allDescendantsOfRemovedReleaseShouldNotBeInProjectContextAnymore() throws UnableToCompleteActionException {
		final Release removedRelease = rootRelease.getChild(0);
		final List<Release> removedChildrenReleases = removedRelease.getChildren();
		new ReleaseRemoveAction(removedRelease.getId()).execute(context, actionContext);

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

		new ReleaseRemoveAction(removedRelease.getId()).execute(context, actionContext);

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

		new ReleaseRemoveAction(removedRelease.getId()).execute(context, actionContext);

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
		final ModelAction rollbackAction = new ReleaseRemoveAction(removedRelease.getId()).execute(context, actionContext);

		assertEquals(2, rootRelease.getChildren().size());
		assertFalse(rootRelease.getChildren().contains(removedRelease));

		rollbackAction.execute(context, actionContext);

		assertEquals(3, rootRelease.getChildren().size());
		assertTrue(rootRelease.getChildren().contains(removedRelease));
	}

	@Test
	public void rollbackShouldReinsertRemovedReleaseInTheSamePositionItWasBeforeRemoval() throws UnableToCompleteActionException {
		final int position = 0;
		final Release removedRelease = rootRelease.getChild(position);
		final ModelAction rollbackAction = new ReleaseRemoveAction(removedRelease.getId()).execute(context, actionContext);

		assertEquals(2, rootRelease.getChildren().size());
		assertFalse(rootRelease.getChildren().contains(removedRelease));
		assertFalse(rootRelease.getChild(0).getDescription().equals(removedRelease.getDescription()));

		rollbackAction.execute(context, actionContext);

		assertEquals(3, rootRelease.getChildren().size());
		assertEquals(position, rootRelease.getChildren().indexOf(removedRelease));
	}

	@Test
	public void rollbackShouldRecreateEntireReleaseHierarchyInTheSameOrder() throws UnableToCompleteActionException {
		final Release removedRelease = rootRelease.getChild(0);
		final Release childRelease0 = removedRelease.getChild(0);
		final Release childRelease1 = removedRelease.getChild(1);
		final Release childRelease2 = removedRelease.getChild(2);

		final ModelAction rollbackAction = new ReleaseRemoveAction(removedRelease.getId()).execute(context, actionContext);

		assertEquals(2, rootRelease.getChildren().size());
		assertFalse(rootRelease.getChildren().contains(removedRelease));

		rollbackAction.execute(context, actionContext);

		assertEquals(3, rootRelease.getChildren().size());
		assertEquals(0, rootRelease.getChildren().indexOf(removedRelease));
		assertEquals(0, rootRelease.getChild(0).getChildren().indexOf(childRelease0));
		assertEquals(1, rootRelease.getChild(0).getChildren().indexOf(childRelease1));
		assertEquals(2, rootRelease.getChild(0).getChildren().indexOf(childRelease2));
	}

	@Test
	public void removedReleaseShouldBeInProjectContextAfterRollback() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release removedRelease = rootRelease.getChild(2);
		final ModelAction rollbackAction = new ReleaseRemoveAction(removedRelease.getId()).execute(context, actionContext);

		try {
			context.findRelease(removedRelease.getId());
			fail();
		}
		catch (final ReleaseNotFoundException e) {}

		rollbackAction.execute(context, actionContext);

		assertEquals(removedRelease, context.findRelease(removedRelease.getId()));
	}

	@Test
	public void allDescendantsOfRemovedReleaseShouldBeInProjectContextAfterRollback() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release removedRelease = rootRelease.getChild(2);
		final ModelAction rollbackAction = new ReleaseRemoveAction(removedRelease.getId()).execute(context, actionContext);

		for (final Release childRelease : removedRelease.getChildren()) {
			try {
				context.findRelease(childRelease.getId());
				fail();
			}
			catch (final ReleaseNotFoundException e) {}
		}

		rollbackAction.execute(context, actionContext);

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

		final ReleaseRemoveRollbackAction rollbackAction = new ReleaseRemoveAction(removedRelease.getId()).execute(context, actionContext);

		assertTrue(removedRelease.getScopeList().isEmpty());
		assertFalse(removedRelease.getScopeList().contains(scope1));
		assertFalse(removedRelease.getScopeList().contains(scope2));
		assertNull(scope1.getRelease());
		assertNull(scope2.getRelease());

		rollbackAction.execute(context, actionContext);

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
		actionExecutionManager.doUserAction(new ReleaseRemoveAction(removedRelease.getId()), context, actionContext);

		try {
			context.findRelease(removedRelease.getId());
			fail();
		}
		catch (final ReleaseNotFoundException e) {
			// Removed release should not be in context anymore. This exception is expected. If it is not thrown, the test fails.
		}

		for (int i = 0; i < 20; i++) {
			actionExecutionManager.undoUserAction(context, actionContext);

			assertEquals(3, rootRelease.getChildren().size());
			assertEquals(0, rootRelease.getChildren().indexOf(removedRelease));
			assertEquals(0, rootRelease.getChild(0).getChildren().indexOf(childRelease0));
			assertEquals(1, rootRelease.getChild(0).getChildren().indexOf(childRelease1));
			assertEquals(2, rootRelease.getChild(0).getChildren().indexOf(childRelease2));

			assertEquals(removedRelease, context.findRelease(removedRelease.getId()));
			assertEquals(childRelease0, context.findRelease(childRelease0.getId()));
			assertEquals(childRelease1, context.findRelease(childRelease1.getId()));
			assertEquals(childRelease2, context.findRelease(childRelease2.getId()));

			actionExecutionManager.redoUserAction(context, actionContext);

			assertEquals(2, rootRelease.getChildren().size());
			assertFalse(rootRelease.getChildren().contains(removedRelease));
			assertFalse(rootRelease.getChildren().contains(removedRelease));

		}
	}

	@Test
	public void shouldRemoveAllRelatedAnntoations() throws Exception {
		final User user = UserTestUtils.createUser();
		final UUID releaseId = rootRelease.getChild(0).getId();

		for (int i = 0; i < 6; i++) {
			context.addAnnotation(releaseId, AnnotationTestUtils.create(user));
		}

		when(actionContext.getUserId()).thenReturn(user.getId());
		new ReleaseRemoveAction(releaseId).execute(context, actionContext);

		assertTrue(context.findAnnotationsFor(releaseId).isEmpty());
	}

	@Test
	public void rollbackShouldReAddAllRemovedAnntoations() throws Exception {
		final User user = UserTestUtils.createUser();
		context.addUser(user);
		final UUID releaseId = rootRelease.getChild(0).getId();

		final List<Annotation> annotationsList = new ArrayList<Annotation>();
		annotationsList.add(AnnotationTestUtils.create(user));
		annotationsList.add(AnnotationTestUtils.create(user));

		for (final Annotation annotation : annotationsList) {
			context.addAnnotation(releaseId, annotation);
		}

		when(actionContext.getUserId()).thenReturn(user.getId());
		final ReleaseRemoveRollbackAction rollbackAction = new ReleaseRemoveAction(releaseId).execute(context, actionContext);

		assertTrue(context.findAnnotationsFor(releaseId).isEmpty());

		rollbackAction.execute(context, actionContext);

		assertEquals(annotationsList.size(), context.findAnnotationsFor(releaseId).size());
		assertTrue(context.findAnnotationsFor(releaseId).containsAll(annotationsList));
	}

	@Test
	public void shouldRemoveAllRelatedChecklists() throws Exception {
		final UUID releaseId = rootRelease.getChild(0).getId();

		for (int i = 0; i < 6; i++)
			context.addChecklist(releaseId, ChecklistTestUtils.create());

		new ReleaseRemoveAction(releaseId).execute(context, actionContext);

		assertTrue(context.findChecklistsFor(releaseId).isEmpty());
	}

	@Test
	public void rollbackShouldReAddAllRemovedChecklists() throws Exception {
		final UUID releaseId = rootRelease.getChild(0).getId();

		final List<Checklist> checklistsList = new ArrayList<Checklist>();
		checklistsList.add(ChecklistTestUtils.create());
		checklistsList.add(ChecklistTestUtils.create());

		for (final Checklist checklist : checklistsList) {
			context.addChecklist(releaseId, checklist);
		}

		final ReleaseRemoveRollbackAction rollbackAction = new ReleaseRemoveAction(releaseId).execute(context, actionContext);

		assertTrue(context.findChecklistsFor(releaseId).isEmpty());

		rollbackAction.execute(context, actionContext);

		assertEquals(checklistsList.size(), context.findChecklistsFor(releaseId).size());
		assertTrue(context.findChecklistsFor(releaseId).containsAll(checklistsList));
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ReleaseRemoveActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ReleaseRemoveAction.class;
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ReleaseRemoveAction(new UUID());
	}
}
