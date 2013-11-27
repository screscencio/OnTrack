package br.com.oncast.ontrack.shared.model.action.scope;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.server.services.exportImport.xml.UserActionTestUtils;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeRemoveActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeAddAssociatedUserAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveRollbackAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.metadata.HumanIdMetadata;
import br.com.oncast.ontrack.shared.model.metadata.MetadataFactory;
import br.com.oncast.ontrack.shared.model.metadata.MetadataType;
import br.com.oncast.ontrack.shared.model.metadata.UserAssociationMetadata;
import br.com.oncast.ontrack.shared.model.metadata.exceptions.MetadataNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;
import br.com.oncast.ontrack.utils.model.AnnotationTestUtils;
import br.com.oncast.ontrack.utils.model.ChecklistTestUtils;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.when;

public class ScopeRemoveActionTest extends ModelActionTest {

	private Scope rootScope;
	private Scope child1Level1;
	private Scope child2Level1;
	private ProjectContext context;
	private Scope child1Level2;
	private Scope child1Level3;

	@Before
	public void setUp() {
		rootScope = ScopeTestUtils.createScope("root");
		child1Level1 = ScopeTestUtils.createScope("child1Level1");
		child2Level1 = ScopeTestUtils.createScope("child2Level1");
		child1Level2 = ScopeTestUtils.createScope("child1Level2");
		child1Level3 = ScopeTestUtils.createScope("child2Level2");
		rootScope.add(child1Level1);
		rootScope.add(child2Level1);
		child1Level1.add(child1Level2);
		child1Level2.add(child1Level3);

		context = ProjectTestUtils.createProjectContext(rootScope, ReleaseTestUtils.createRelease(""));
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
		final Scope child1Level32 = ScopeTestUtils.createScope("child1Level3.2");
		final Scope child1Level33 = ScopeTestUtils.createScope("child1Level3.3");
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
		final ActionExecutionManager actionExecutionManager = ActionExecutionTestUtils.createManager(context);
		actionExecutionManager.doUserAction(UserActionTestUtils.create(new ScopeRemoveAction(child1Level1.getId()), actionContext));

		for (int i = 0; i < 20; i++) {
			actionExecutionManager.undoUserAction();

			assertEquals(2, rootScope.getChildren().size());
			assertEquals(child1Level1, rootScope.getChildren().get(0));
			assertEquals(child2Level1, rootScope.getChildren().get(1));
			assertEquals(child1Level2, rootScope.getChildren().get(0).getChildren().get(0));
			assertEquals(child1Level3, rootScope.getChildren().get(0).getChildren().get(0).getChildren().get(0));

			actionExecutionManager.redoUserAction();

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

		final UserRepresentation user = UserRepresentationTestUtils.createUser();

		annotationsList.add(AnnotationTestUtils.create(user));
		annotationsList.add(AnnotationTestUtils.create(user));

		final UUID scopeId = child1Level1.getId();

		for (final Annotation annotation : annotationsList) {
			context.addAnnotation(scopeId, annotation);
		}

		when(actionContext.getUserId()).thenReturn(user.getId());
		context.addUser(user);

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

	@Test
	public void shouldRemoveAllUserAssociationsOfTheScope() throws Exception {
		final List<UserRepresentation> usersList = new ArrayList<UserRepresentation>();

		usersList.add(UserRepresentationTestUtils.createUser());
		usersList.add(UserRepresentationTestUtils.createUser());

		final UUID scopeId = child1Level1.getId();

		for (final UserRepresentation user : usersList) {
			context.addUser(user);
			new ScopeAddAssociatedUserAction(scopeId, user.getId()).execute(context, actionContext);
		}
		assertTrue(context.hasMetadata(child1Level1, UserAssociationMetadata.getType()));

		new ScopeRemoveAction(scopeId).execute(context, actionContext);

		assertFalse(context.hasMetadata(child1Level1, UserAssociationMetadata.getType()));
	}

	@Test(expected = MetadataNotFoundException.class)
	public void shouldRemoveAnyExistentHumanId() throws Exception {
		final UUID metadataId = new UUID();
		context.addMetadata(MetadataFactory.createHumanIdMetadata(metadataId, child1Level1, "humanId"));
		new ScopeRemoveAction(child1Level1.getId()).execute(context, actionContext);
		context.findMetadata(child1Level1, MetadataType.HUMAN_ID, metadataId);
	}

	@Test
	public void undoShouldAddTheRemovedHumanId() throws Exception {
		final HumanIdMetadata metadata = MetadataFactory.createHumanIdMetadata(new UUID(), child1Level1, "humanId");
		context.addMetadata(metadata);
		final ScopeRemoveRollbackAction undo = new ScopeRemoveAction(child1Level1.getId()).execute(context, actionContext);
		undo.execute(context, actionContext);
		assertEquals(metadata, context.findMetadata(child1Level1, MetadataType.HUMAN_ID, metadata.getId()));
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
