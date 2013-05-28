package br.com.oncast.ontrack.server.services.exportImport.xml;

import static br.com.oncast.ontrack.utils.reflection.ReflectionTestUtils.set;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationDeprecateAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationRemoveAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationRemoveDeprecationAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationVoteAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationVoteRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistAddItemAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistCheckItemAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistCreateAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistEditItemDescriptionAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistRemoveItemAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistRenameAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistUncheckItemAction;
import br.com.oncast.ontrack.shared.model.action.DescriptionCreateAction;
import br.com.oncast.ontrack.shared.model.action.DescriptionRemoveAction;
import br.com.oncast.ontrack.shared.model.action.FileUploadAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentCreateAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentSolveAction;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnCreateAction;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnMoveAction;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnRemoveAction;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnRenameAction;
import br.com.oncast.ontrack.shared.model.action.KanbanLockAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseCreateAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareEndDayAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareEstimatedVelocityAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareStartDayAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRemoveRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRenameAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseScopeUpdatePriorityAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseUpdatePriorityAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAddAssociatedUserAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAddTagAssociationAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindHumanIdAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareDueDateAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareEffortAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareTimeSpentAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertParentAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertParentRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingDownAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingDownRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingUpAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingUpRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveDownAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveRightAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveToAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveUpAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAssociatedUserAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveTagAssociationAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUnbindHumanIdAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.action.TagCreateAction;
import br.com.oncast.ontrack.shared.model.action.TagRemoveAction;
import br.com.oncast.ontrack.shared.model.action.TagUpdateAction;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.color.Color;
import br.com.oncast.ontrack.shared.model.metadata.MetadataFactory;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

public class UserActionTestUtils {

	public static final UUID DEFAULT_USER_ID = new UUID();
	public static final UUID DEFAULT_PROJECT_ID = new UUID();
	public static final String DEFAULT_PROJECT_NAME = "Default project";

	private static long actionCount = 0;
	private static UUID userId = DEFAULT_USER_ID;
	private static UUID projectId = DEFAULT_PROJECT_ID;
	private static String projectName = DEFAULT_PROJECT_NAME;

	public static List<User> createUserList() throws Exception {
		return UserTestUtils.createList(2);
	}

	public static List<Password> createPasswordListFor(final List<User> userList) {
		final List<Password> passwords = new ArrayList<Password>();

		for (final User user : userList) {
			final Password password = new Password();
			password.setUserId(user.getId());
			password.setPassword("password" + user.getId().toString());
			passwords.add(password);
		}
		return passwords;
	}

	public static List<UserAction> createCompleteUserActionList(final UUID projectId) throws Exception {
		setProjectId(projectId);
		final List<UserAction> actionList = createCompleteUserActionList();

		resetProperties();
		return actionList;
	}

	public static List<UserAction> createCompleteUserActionList() throws Exception {
		final List<UserAction> userActions = new ArrayList<UserAction>();
		userActions.add(createReleaseRemoveAction());
		userActions.add(createReleaseScopeUpdatePriorityAction());
		userActions.add(createReleaseUpdatePriorityAction());
		userActions.add(createScopeDeclareEffortAction());
		userActions.add(createScopeDeclareProgressAction());
		userActions.add(createScopeMoveUpAction());
		userActions.add(createScopeMoveDownAction());
		userActions.add(createScopeUpdateAction());
		userActions.add(createReleaseRemoveRollbackAction());
		userActions.add(createReleaseCreateActionDefault());
		userActions.add(createScopeBindReleaseAction());
		userActions.add(createScopeInsertChildRollbackAction());
		userActions.add(createScopeInsertParentRollbackAction());
		userActions.add(createScopeInsertSiblingDownRollbackAction());
		userActions.add(createScopeInsertSiblingUpRollbackAction());
		userActions.add(createScopeRemoveAction());
		userActions.add(createScopeInsertChildAction());
		userActions.add(createScopeInsertParentAction());
		userActions.add(createScopeRemoveRollbackAction());
		userActions.add(createScopeInsertSiblingDownAction());
		userActions.add(createScopeInsertSiblingUpAction());
		userActions.add(createScopeMoveLeftAction());
		userActions.add(createScopeMoveRightAction());
		userActions.add(createKanbanColumnMoveAction());
		userActions.add(createKanbanColumnRenameAction());
		userActions.add(createKanbanColumnRemoveAction());
		userActions.add(createKanbanColumnCreateAction());
		userActions.add(createReleaseDeclareEndDayAction());
		userActions.add(createReleaseDeclareStartDayAction());
		userActions.add(createReleaseDeclareEstimatedVelocityAction());
		userActions.add(createReleaseRenameAction());
		userActions.add(createAnnotationCreateAction());
		userActions.add(createAnnotationRemoveAction());
		userActions.add(createAnnotationVoteAction());
		userActions.add(createAnnotationVoteRemoveAction());
		userActions.add(createTeamInviteAction());
		userActions.add(createFileUploadAction());
		userActions.add(createChecklistCreateAction());
		userActions.add(createChecklistAddItemAction());
		userActions.add(createChecklistCheckItemAction());
		userActions.add(createChecklistUncheckItemAction());
		userActions.add(createChecklistRemoveItemAction());
		userActions.add(createChecklistEditItemDescriptionAction());
		userActions.add(createChecklistRemoveAction());
		userActions.add(createChecklistRenameAction());
		userActions.add(createAnnotationDeprecateAction());
		userActions.add(createAnnotationRemoveDeprecationAction());
		userActions.add(createImpedimentCreateAction());
		userActions.add(createImpedimentRemoveAction());
		userActions.add(createImpedimentSolveAction());
		userActions.add(createScopeAddAssociatedUserAction());
		userActions.add(createScopeRemoveAssociatedUserAction());
		userActions.add(createDescriptionCreateAction());
		userActions.add(createDescriptionRemoveAction());
		userActions.add(createTagCreateAction());
		userActions.add(createTagRemoveAction());
		userActions.add(createTagUpdateAction());
		userActions.add(createScopeAddTagAssociationAction());
		userActions.add(createScopeRemoveTagAssociationAction());
		userActions.add(createScopeMoveToAction());
		userActions.add(createScopeBindHumanIdAction());
		userActions.add(createScopeUnbindHumanIdAction());
		userActions.add(createScopeDeclareTimeSpentAction());
		userActions.add(createScopeDeclareDueDateAction());
		userActions.add(createKanbanLockAction());
		return userActions;
	}

	private static UserAction createKanbanLockAction() throws Exception {
		return createUserAction(new KanbanLockAction(new UUID()));
	}

	private static UserAction createScopeDeclareDueDateAction() throws Exception {
		return createUserAction(new ScopeDeclareDueDateAction(new UUID(), new Date()));
	}

	private static UserAction createScopeDeclareTimeSpentAction() throws Exception {
		return createUserAction(new ScopeDeclareTimeSpentAction(new UUID(), 2F));
	}

	private static UserAction createScopeUnbindHumanIdAction() throws Exception {
		return createUserAction(new ScopeUnbindHumanIdAction(MetadataFactory.createHumanIdMetadata(new UUID(), ScopeTestUtils.createScope(), "")));
	}

	private static UserAction createScopeBindHumanIdAction() throws Exception {
		return createUserAction(new ScopeBindHumanIdAction(new UUID(), "id"));
	}

	private static UserAction createScopeMoveToAction() throws Exception {
		return createUserAction(new ScopeMoveToAction(new UUID(), new UUID(), 0));
	}

	private static UserAction createScopeRemoveTagAssociationAction() throws Exception {
		return createUserAction(new ScopeRemoveTagAssociationAction(new UUID(), new UUID()));
	}

	private static UserAction createScopeAddTagAssociationAction() throws Exception {
		return createUserAction(new ScopeAddTagAssociationAction(new UUID(), new UUID()));
	}

	private static UserAction createTagUpdateAction() throws Exception {
		return createUserAction(new TagUpdateAction(new UUID(), "description", Color.RED, Color.BLUE));
	}

	private static UserAction createTagRemoveAction() throws Exception {
		return createUserAction(new TagRemoveAction(new UUID()));
	}

	private static UserAction createTagCreateAction() throws Exception {
		return createUserAction(new TagCreateAction("description", Color.BLUE, Color.RED));
	}

	private static UserAction createDescriptionRemoveAction() throws Exception {
		return createUserAction(new DescriptionRemoveAction(new UUID(), new UUID(), false));
	}

	private static UserAction createDescriptionCreateAction() throws Exception {
		return createUserAction(new DescriptionCreateAction(new UUID(), "new description"));
	}

	public static List<UserAction> createCompleteUserActionListOrderedById() throws Exception {
		final List<UserAction> actionList = createCompleteUserActionList();
		changeEachActionIdToItsIndex(actionList);
		return actionList;
	}

	private static void changeEachActionIdToItsIndex(final List<UserAction> actionList) throws Exception {
		for (int i = 0; i < actionList.size(); i++) {
			final UserAction userAction = actionList.get(i);
			set(userAction, "id", i);
		}
	}

	public static List<UserAction> createRandomUserActionList() throws Exception {
		final List<UserAction> actionList = createCompleteUserActionList();
		shuffle(actionList);
		return actionList;
	}

	public static List<UserAction> createRandomUserActionList(final UUID projectId, final String projectName) throws Exception {
		setProjectName(projectName);

		final List<UserAction> actionList = createCompleteUserActionList(projectId);
		shuffle(actionList);

		resetProperties();
		return actionList;
	}

	private static void shuffle(final List<UserAction> actionList) {
		Collections.shuffle(actionList);

		for (int i = 0; i < new Random().nextInt(10); i++) {
			actionList.remove(0);
		}
	}

	public static UserAction createScopeAddAssociatedUserAction() throws Exception {
		return createUserAction(new ScopeAddAssociatedUserAction(new UUID(), new UUID()));
	}

	public static UserAction createScopeRemoveAssociatedUserAction() throws Exception {
		return createUserAction(new ScopeRemoveAssociatedUserAction(new UUID(), new UUID()));
	}

	public static UserAction createChecklistRenameAction() throws Exception {
		return createUserAction(new ChecklistRenameAction(new UUID(), new UUID(), "new title"));
	}

	public static UserAction createChecklistRemoveAction() throws Exception {
		return createUserAction(new ChecklistRemoveAction(new UUID(), new UUID()));
	}

	public static UserAction createChecklistRemoveItemAction() throws Exception {
		return createUserAction(new ChecklistRemoveItemAction(new UUID(), new UUID(), new UUID()));
	}

	public static UserAction createChecklistEditItemDescriptionAction() throws Exception {
		return createUserAction(new ChecklistEditItemDescriptionAction(new UUID(), new UUID(), new UUID(), "new description"));
	}

	public static UserAction createChecklistUncheckItemAction() throws Exception {
		return createUserAction(new ChecklistUncheckItemAction(new UUID(), new UUID(), new UUID()));
	}

	public static UserAction createChecklistCheckItemAction() throws Exception {
		return createUserAction(new ChecklistCheckItemAction(new UUID(), new UUID(), new UUID()));
	}

	public static UserAction createChecklistAddItemAction() throws Exception {
		return createUserAction(new ChecklistAddItemAction(new UUID(), new UUID(), "checklist item description"));
	}

	public static UserAction createChecklistCreateAction() throws Exception {
		return createUserAction(new ChecklistCreateAction(new UUID(), "checklist title"));
	}

	public static UserAction createFileUploadAction() throws Exception {
		return createUserAction(new FileUploadAction(new UUID(), "fileName.extension", "path/to/file"));
	}

	public static UserAction createTeamInviteAction() throws Exception {
		return createUserAction(new TeamInviteAction(new UUID()));
	}

	public static UserAction createImpedimentCreateAction() throws Exception {
		return createUserAction(new ImpedimentCreateAction(new UUID(), new UUID()));
	}

	public static UserAction createImpedimentRemoveAction() throws Exception {
		return createUserAction(new ImpedimentRemoveAction(new UUID(), new UUID(), AnnotationType.SIMPLE));
	}

	public static UserAction createImpedimentSolveAction() throws Exception {
		return createUserAction(new ImpedimentSolveAction(new UUID(), new UUID()));
	}

	public static UserAction createAnnotationVoteRemoveAction() throws Exception {
		return createUserAction(new AnnotationVoteRemoveAction(new UUID(), new UUID()));
	}

	public static UserAction createAnnotationVoteAction() throws Exception {
		return createUserAction(new AnnotationVoteAction(new UUID(), new UUID()));
	}

	public static UserAction createAnnotationRemoveDeprecationAction() throws Exception {
		return createUserAction(new AnnotationRemoveDeprecationAction(new UUID(), new UUID()));
	}

	public static UserAction createAnnotationDeprecateAction() throws Exception {
		return createUserAction(new AnnotationDeprecateAction(new UUID(), new UUID()));
	}

	public static UserAction createAnnotationRemoveAction() throws Exception {
		return createUserAction(new AnnotationRemoveAction(new UUID(), new UUID()));
	}

	public static UserAction createAnnotationCreateAction() throws Exception {
		return createAnnotationCreateAction("message");
	}

	public static UserAction createReleaseDeclareEndDayAction() throws Exception {
		return createUserAction(new ReleaseDeclareEndDayAction(new UUID(), new Date()));
	}

	public static UserAction createReleaseDeclareStartDayAction() throws Exception {
		return createUserAction(new ReleaseDeclareStartDayAction(new UUID(), new Date()));
	}

	public static UserAction createReleaseDeclareEstimatedVelocityAction() throws Exception {
		return createUserAction(new ReleaseDeclareEstimatedVelocityAction(new UUID(), 2f));
	}

	public static UserAction createReleaseRenameAction() throws Exception {
		return createUserAction(new ReleaseRenameAction(new UUID(), "new release name"));
	}

	public static UserAction createScopeMoveRightAction() throws Exception {
		final ScopeMoveRightAction scopeMoveRight = new
				ScopeMoveRightAction(new UUID(), 1, new ArrayList<ModelAction>());
		return createUserAction(scopeMoveRight);
	}

	public static UserAction createScopeMoveLeftAction() throws Exception {
		final ScopeMoveLeftAction scopeMoveLeft = new
				ScopeMoveLeftAction(new UUID(), new ArrayList<ModelAction>());
		return createUserAction(scopeMoveLeft);
	}

	public static UserAction createScopeInsertSiblingUpAction() throws Exception {
		final ScopeInsertSiblingUpAction scopeInsertSibling = new
				ScopeInsertSiblingUpAction(new UUID(), new UUID(), "pattern");
		return createUserAction(scopeInsertSibling);
	}

	public static UserAction createScopeInsertSiblingDownAction() throws Exception {
		final ScopeInsertSiblingDownAction scopeInsertSibling = new
				ScopeInsertSiblingDownAction(new UUID(), new UUID(), "pattern");
		return createUserAction(scopeInsertSibling);
	}

	public static UserAction createScopeRemoveRollbackAction() throws Exception {
		final ScopeRemoveRollbackAction scopeRemoveRollback = new
				ScopeRemoveRollbackAction(new UUID(), new UUID(), "text", 1, new ArrayList<ModelAction>(), new ArrayList<ScopeRemoveRollbackAction>());
		return createUserAction(scopeRemoveRollback);
	}

	public static UserAction createScopeInsertParentAction() throws Exception {
		final ScopeInsertParentAction scopeInsertParent = new ScopeInsertParentAction(new UUID(), "text");
		return createUserAction(scopeInsertParent);
	}

	public static UserAction createScopeInsertChildAction() throws Exception {
		final ScopeInsertChildAction scopeInsertChild = new ScopeInsertChildAction(new UUID(), "text");
		return createUserAction(scopeInsertChild);
	}

	public static UserAction createScopeRemoveAction() throws Exception {
		final ScopeRemoveAction scopeRemove = new ScopeRemoveAction(new UUID());
		return createUserAction(scopeRemove);
	}

	public static UserAction createScopeInsertSiblingUpRollbackAction() throws Exception {
		final ScopeInsertSiblingUpRollbackAction insertSiblingRollback = new ScopeInsertSiblingUpRollbackAction(new UUID(),
				new ScopeUpdateAction(
						new UUID(), "descricao @release %d #3"));
		return createUserAction(insertSiblingRollback);
	}

	public static UserAction createScopeInsertSiblingDownRollbackAction() throws Exception {
		final ScopeInsertSiblingDownRollbackAction insertSiblingRollback = new ScopeInsertSiblingDownRollbackAction(new UUID(),
				new ScopeUpdateAction(
						new UUID(), "descricao @release %d #3"));
		return createUserAction(insertSiblingRollback);
	}

	public static UserAction createScopeInsertParentRollbackAction() throws Exception {
		final ScopeInsertParentRollbackAction insertParentRollback = new ScopeInsertParentRollbackAction(new UUID(), new UUID(), new ScopeUpdateAction(
				new UUID(), "descricao @release %d #3"));
		return createUserAction(insertParentRollback);
	}

	public static UserAction createScopeInsertChildRollbackAction() throws Exception {
		final ScopeInsertChildRollbackAction insertChildRollback = new ScopeInsertChildRollbackAction(new UUID(), new ArrayList<ModelAction>());
		return createUserAction(insertChildRollback);
	}

	public static UserAction createScopeBindReleaseAction() throws Exception {
		final ScopeBindReleaseAction bindReleaseAction = new ScopeBindReleaseAction(new UUID(), "release");
		return createUserAction(bindReleaseAction);
	}

	public static UserAction createReleaseCreateActionDefault() throws Exception {
		final ReleaseCreateAction createAction = new ReleaseCreateAction("description");
		set(createAction, "parentReleaseId", new UUID());
		return createUserAction(createAction);
	}

	public static UserAction createReleaseRemoveAction() throws Exception {
		return createUserAction(new ReleaseRemoveAction(new UUID()));
	}

	public static UserAction createReleaseRemoveRollbackAction() throws Exception {
		final ReleaseRemoveRollbackAction releaseRemoveRollbackAction = new ReleaseRemoveRollbackAction(new UUID(), new UUID(), "", 1,
				new ArrayList<ReleaseRemoveRollbackAction>(),
				new ArrayList<ModelAction>());
		return createUserAction(releaseRemoveRollbackAction);
	}

	public static UserAction createReleaseScopeUpdatePriorityAction() throws Exception {
		final ReleaseScopeUpdatePriorityAction releaseScopeUpdatePriorityAction = new ReleaseScopeUpdatePriorityAction(new UUID(), new UUID(), 1);
		return createUserAction(releaseScopeUpdatePriorityAction);
	}

	public static UserAction createReleaseUpdatePriorityAction() throws Exception {
		final ReleaseUpdatePriorityAction releaseScopeUpdatePriorityAction = new ReleaseUpdatePriorityAction(new UUID(), 1);
		return createUserAction(releaseScopeUpdatePriorityAction);
	}

	public static UserAction createScopeDeclareEffortAction() throws Exception {
		final ScopeDeclareEffortAction scopeDeclareEffortAction = new ScopeDeclareEffortAction(new UUID(), true, 1);
		return createUserAction(scopeDeclareEffortAction);
	}

	public static UserAction createScopeDeclareProgressAction() throws Exception {
		final ScopeDeclareProgressAction scopeDeclareProgressAction = new ScopeDeclareProgressAction(new UUID(), "text");
		return createUserAction(scopeDeclareProgressAction);
	}

	public static UserAction createScopeMoveDownAction() throws Exception {
		final ScopeMoveDownAction scopeMoveDownAction = new ScopeMoveDownAction(new UUID());
		return createUserAction(scopeMoveDownAction);
	}

	public static UserAction createScopeUpdateAction() throws Exception {
		final ScopeUpdateAction scopeMoveDownAction = new ScopeUpdateAction(new UUID(), "descricao @release %d #3");
		return createUserAction(scopeMoveDownAction);
	}

	public static UserAction createScopeMoveUpAction() throws Exception {
		final ScopeMoveUpAction scopeMoveUpAction = new ScopeMoveUpAction(new UUID());
		return createUserAction(scopeMoveUpAction);
	}

	public static UserAction createKanbanColumnMoveAction() throws Exception {
		final KanbanColumnMoveAction action = new KanbanColumnMoveAction(new UUID(), "description", 2);
		return createUserAction(action);
	}

	public static UserAction createKanbanColumnRenameAction() throws Exception {
		final KanbanColumnRenameAction action = new KanbanColumnRenameAction(new UUID(), "description", "new Description");
		return createUserAction(action);
	}

	public static UserAction createKanbanColumnRemoveAction() throws Exception {
		final KanbanColumnRemoveAction action = new KanbanColumnRemoveAction(new UUID(), "description");
		return createUserAction(action);
	}

	public static UserAction createKanbanColumnCreateAction() throws Exception {
		final ArrayList<ModelAction> rollbackActions = new ArrayList<ModelAction>();
		rollbackActions.add(new ScopeDeclareProgressAction(new UUID(), "newProgressDescription"));
		final KanbanColumnCreateAction action = new KanbanColumnCreateAction(new UUID(), "description", false, 2, rollbackActions);
		return createUserAction(action);
	}

	public static UserAction createUserAction(final ModelAction action) throws Exception {
		final UserAction userAction = new UserAction();
		set(userAction, "projectRepresentation", ProjectTestUtils.createRepresentation(projectId, projectName));
		set(userAction, "id", ++actionCount);
		set(userAction, "userId", userId);
		// Generate a different time stamp so it is like an id, so time stamp equality is like action equality.
		set(userAction, "timestamp", new Date(new Random().nextInt(1000000)));
		return set(userAction, "action", action);
	}

	private static void setProjectName(final String projectName) {
		UserActionTestUtils.projectName = projectName;
	}

	private static void setProjectId(final UUID projectId) {
		UserActionTestUtils.projectId = projectId;
	}

	private static void setUserId(final UUID userId) {
		UserActionTestUtils.userId = userId;
	}

	private static void resetProperties() {
		UserActionTestUtils.projectId = DEFAULT_PROJECT_ID;
		UserActionTestUtils.projectName = DEFAULT_PROJECT_NAME;
		UserActionTestUtils.userId = DEFAULT_USER_ID;
	}

	public static List<UserAction> createRandomUserActionList(final UUID projectId, final UUID userId) throws Exception {
		setProjectId(projectId);
		setUserId(userId);
		final List<UserAction> actions = createRandomUserActionList();
		resetProperties();
		return actions;
	}

	public static UserAction createAnnotationCreateAction(final String message) throws Exception {
		return createUserAction(new AnnotationCreateAction(new UUID(), message, new UUID()));
	}
}
