package br.com.oncast.ontrack.shared.model.action.scope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeRemoveActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveRollbackAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.AnnotationTestUtils;
import br.com.oncast.ontrack.utils.ChecklistTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class ScopeRemoveActionTest extends ModelActionTest {

	private Scope rootScope;
	private Scope child1Level1;
	private Scope child2Level1;
	private ProjectContext context;
	private Scope child1Level2;
	private Scope child1Level3;

	@Before
	public void setUp() {
		rootScope = new Scope("root");
		child1Level1 = new Scope("child1Level1");
		child2Level1 = new Scope("child2Level1");
		child1Level2 = new Scope("child1Level2");
		child1Level3 = new Scope("child2Level2");
		rootScope.add(child1Level1);
		rootScope.add(child2Level1);
		child1Level1.add(child1Level2);
		child1Level2.add(child1Level3);

		context = ProjectTestUtils.createProjectContext(rootScope, ReleaseFactoryTestUtil.create(""));
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void rootScopeCantBeRemoved() throws UnableToCompleteActionException {
		new ScopeRemoveAction(rootScope.getId()).execute(context, actionContext);
	}

	@Test
	public void aRemovedScopeShouldBeRemovedFromFather() throws UnableToCompleteActionException {
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child2Level1, rootScope.getChildren().get(1));

		new ScopeRemoveAction(child2Level1.getId()).execute(context, actionContext);

		assertEquals(1, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child1Level2, rootScope.getChildren().get(0).getChildren().get(0));
		assertEquals(child1Level3, rootScope.getChildren().get(0).getChildren().get(0).getChildren().get(0));
	}

	@Test
	public void rollbackShouldRecreateEntireScopeHierarchyInTheSameOrder() throws UnableToCompleteActionException {
		final Scope child1Level32 = new Scope("child1Level3.2");
		final Scope child1Level33 = new Scope("child1Level3.3");
		child1Level2.add(child1Level32);
		child1Level2.add(child1Level33);

		final ScopeRemoveRollbackAction rollbackAction = new ScopeRemoveAction(child1Level1.getId()).execute(context, actionContext);
		rollbackAction.execute(context, actionContext);

		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child1Level2, rootScope.getChildren().get(0).getChildren().get(0));
		assertEquals(child1Level3, rootScope.getChildren().get(0).getChildren().get(0).getChildren().get(0));
		assertEquals(child1Level32, rootScope.getChildren().get(0).getChildren().get(0).getChildren().get(1));
		assertEquals(child1Level33, rootScope.getChildren().get(0).getChildren().get(0).getChildren().get(2));
	}

	@Test
	public void rollbackShouldRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child2Level1, rootScope.getChildren().get(1));

		final ScopeRemoveAction removeScopeAction = new ScopeRemoveAction(child2Level1.getId());
		final ModelAction rollbackAction = removeScopeAction.execute(context, actionContext);

		assertEquals(1, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));

		rollbackAction.execute(context, actionContext);

		assertEquals(2, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child2Level1, rootScope.getChildren().get(1));
	}

	@Test
	public void executeShouldRemoveChildsFromRemovedScope() throws UnableToCompleteActionException {
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child2Level1, rootScope.getChildren().get(1));
		assertEquals(child1Level2, rootScope.getChildren().get(0).getChildren().get(0));
		assertEquals(child1Level3, rootScope.getChildren().get(0).getChildren().get(0).getChildren().get(0));

		final ScopeRemoveAction removeScopeAction = new ScopeRemoveAction(child1Level1.getId());
		removeScopeAction.execute(context, actionContext);

		assertEquals(1, rootScope.getChildren().size());
		assertEquals(child2Level1, rootScope.getChildren().get(0));
		assertNull(child1Level1.getParent());
		assertNull(child1Level2.getParent());
		assertNull(child1Level3.getParent());
		assertEquals(child1Level1.getChildren().size(), 0);
		assertEquals(child1Level2.getChildren().size(), 0);
		assertEquals(child1Level3.getChildren().size(), 0);
	}

	@Test
	public void rollbackShouldRevertChildsFromRemovedScope() throws UnableToCompleteActionException {
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child2Level1, rootScope.getChildren().get(1));
		assertEquals(child1Level2, rootScope.getChildren().get(0).getChildren().get(0));
		assertEquals(child1Level3, rootScope.getChildren().get(0).getChildren().get(0).getChildren().get(0));

		final ScopeRemoveAction removeScopeAction = new ScopeRemoveAction(child1Level1.getId());
		final ModelAction rollbackAction = removeScopeAction.execute(context, actionContext);

		assertEquals(1, rootScope.getChildren().size());
		assertEquals(child2Level1, rootScope.getChildren().get(0));
		assertNull(child1Level1.getParent());
		assertNull(child1Level2.getParent());
		assertNull(child1Level3.getParent());
		assertEquals(child1Level1.getChildren().size(), 0);
		assertEquals(child1Level2.getChildren().size(), 0);
		assertEquals(child1Level3.getChildren().size(), 0);

		rollbackAction.execute(context, actionContext);

		assertEquals(2, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child2Level1, rootScope.getChildren().get(1));
		assertEquals(child1Level2, rootScope.getChildren().get(0).getChildren().get(0));
		assertEquals(child1Level3, rootScope.getChildren().get(0).getChildren().get(0).getChildren().get(0));

	}

	@Test
	public void executeARolledbackActionShouldRemoveChildScopes() throws UnableToCompleteActionException {
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child2Level1, rootScope.getChildren().get(1));
		assertEquals(child1Level2, rootScope.getChildren().get(0).getChildren().get(0));
		assertEquals(child1Level3, rootScope.getChildren().get(0).getChildren().get(0).getChildren().get(0));

		final ScopeRemoveAction removeScopeAction = new ScopeRemoveAction(child1Level1.getId());
		final ModelAction rollbackAction = removeScopeAction.execute(context, actionContext);

		assertEquals(1, rootScope.getChildren().size());
		assertEquals(child2Level1, rootScope.getChildren().get(0));
		assertNull(child1Level1.getParent());
		assertNull(child1Level2.getParent());
		assertNull(child1Level3.getParent());
		assertEquals(child1Level1.getChildren().size(), 0);
		assertEquals(child1Level2.getChildren().size(), 0);
		assertEquals(child1Level3.getChildren().size(), 0);

		final ModelAction redoAction = rollbackAction.execute(context, actionContext);

		assertEquals(2, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child2Level1, rootScope.getChildren().get(1));
		assertEquals(child1Level2, rootScope.getChildren().get(0).getChildren().get(0));
		assertEquals(child1Level3, rootScope.getChildren().get(0).getChildren().get(0).getChildren().get(0));

		redoAction.execute(context, actionContext);

		assertEquals(1, rootScope.getChildren().size());
		assertEquals(child2Level1, rootScope.getChildren().get(0));
		assertNull(child1Level1.getParent());
		assertNull(child1Level2.getParent());
		assertNull(child1Level3.getParent());
		assertEquals(child1Level1.getChildren().size(), 0);
		assertEquals(child1Level2.getChildren().size(), 0);
		assertEquals(child1Level3.getChildren().size(), 0);
	}

	@Test
	public void shouldHandleRemovalCorrectlyAfterMultipleUndosAndRedos() throws UnableToCompleteActionException {
		final ActionExecutionManager actionExecutionManager = new ActionExecutionManager(Mockito.mock(ActionExecutionListener.class));
		actionExecutionManager.doUserAction(new ScopeRemoveAction(child1Level1.getId()), context, actionContext);

		for (int i = 0; i < 20; i++) {
			actionExecutionManager.undoUserAction(context, actionContext);

			assertEquals(2, rootScope.getChildren().size());
			assertEquals(child1Level1, rootScope.getChildren().get(0));
			assertEquals(child2Level1, rootScope.getChildren().get(1));
			assertEquals(child1Level2, rootScope.getChildren().get(0).getChildren().get(0));
			assertEquals(child1Level3, rootScope.getChildren().get(0).getChildren().get(0).getChildren().get(0));

			actionExecutionManager.redoUserAction(context, actionContext);

			assertEquals(1, rootScope.getChildren().size());
			assertEquals(child2Level1, rootScope.getChildren().get(0));
			assertNull(child1Level1.getParent());
			assertNull(child1Level2.getParent());
			assertNull(child1Level3.getParent());
			assertEquals(child1Level1.getChildren().size(), 0);
			assertEquals(child1Level2.getChildren().size(), 0);
			assertEquals(child1Level3.getChildren().size(), 0);
		}
	}

	@Test
	public void shouldRemoveAnnotationsBoundToTheScope() throws Exception {
		final List<Annotation> annotationsList = new ArrayList<Annotation>();

		final User user = UserTestUtils.createUser();

		annotationsList.add(AnnotationTestUtils.create(user));
		annotationsList.add(AnnotationTestUtils.create(user));

		final UUID scopeId = child1Level1.getId();

		for (final Annotation annotation : annotationsList) {
			context.addAnnotation(scopeId, annotation);
		}

		when(actionContext.getUserEmail()).thenReturn(user.getEmail());
		new ScopeRemoveAction(scopeId).execute(context, actionContext);

		assertTrue(context.findAnnotationsFor(scopeId).isEmpty());
	}

	@Test
	public void shouldRemoveAllChecklistsBoundToTheScope() throws Exception {
		final List<Checklist> checklistsList = new ArrayList<Checklist>();

		checklistsList.add(ChecklistTestUtils.create());
		checklistsList.add(ChecklistTestUtils.create());

		final UUID scopeId = child1Level1.getId();

		for (final Checklist checklist : checklistsList) {
			context.addChecklist(scopeId, checklist);
		}

		new ScopeRemoveAction(scopeId).execute(context, actionContext);

		assertTrue(context.findChecklistsFor(scopeId).isEmpty());
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ScopeRemoveActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ScopeRemoveAction.class;
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ScopeRemoveAction(new UUID());
	}
}
