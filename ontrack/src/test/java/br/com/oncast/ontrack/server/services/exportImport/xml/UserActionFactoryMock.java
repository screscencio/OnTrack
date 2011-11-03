package br.com.oncast.ontrack.server.services.exportImport.xml;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.oncast.ontrack.server.business.UserAction;
import br.com.oncast.ontrack.server.model.Password;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ReleaseCreateActionDefault;
import br.com.oncast.ontrack.shared.model.actions.ReleaseRemoveAction;
import br.com.oncast.ontrack.shared.model.actions.ReleaseRemoveRollbackAction;
import br.com.oncast.ontrack.shared.model.actions.ReleaseScopeUpdatePriorityAction;
import br.com.oncast.ontrack.shared.model.actions.ReleaseUpdatePriorityAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeDeclareEffortAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertChildRollbackAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertParentAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertParentRollbackAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertSiblingDownAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertSiblingDownRollbackAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertSiblingUpAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertSiblingUpRollbackAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeMoveDownAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeMoveRightAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeMoveUpAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeRemoveRollbackAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UserActionFactoryMock {

	public static List<User> createUserList() {
		final List<User> users = new ArrayList<User>();

		final User user1 = new User();
		user1.setEmail("user1@email");
		user1.setId(1);

		final User user2 = new User();
		user2.setEmail("user2@email");
		user2.setId(2);

		users.add(user1);
		users.add(user2);

		return users;
	}

	public static List<Password> createPasswordList() {
		final List<Password> passwords = new ArrayList<Password>();

		final Password password1 = new Password();
		password1.setUserId(1);
		password1.setPassword("password1");

		final Password password2 = new Password();
		password2.setUserId(2);
		password2.setPassword("password2");

		passwords.add(password1);
		passwords.add(password2);

		return passwords;
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
		return userActions;
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

	public static UserAction createUserAction(final ModelAction action) throws Exception {
		final UserAction userAction = new UserAction();
		set(userAction, "id", 1);
		set(userAction, "timestamp", new Date());
		return set(userAction, "action", action);
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
		final ReleaseCreateActionDefault createAction = new ReleaseCreateActionDefault("description");
		set(createAction, "parentReleaseId", new UUID());
		return createUserAction(createAction);
	}

	public static UserAction createReleaseRemoveAction() throws Exception {
		return createUserAction(new ReleaseRemoveAction(new UUID()));
	}

	public static UserAction createReleaseRemoveRollbackAction() throws Exception {
		final ReleaseRemoveRollbackAction releaseRemoveRollbackAction = new ReleaseRemoveRollbackAction(new UUID(), new UUID(), "", 1,
				new ArrayList<ReleaseRemoveRollbackAction>(), new ArrayList<ScopeBindReleaseAction>());
		return createUserAction(releaseRemoveRollbackAction);
	}

	public static UserAction createReleaseScopeUpdatePriorityAction() throws Exception {
		final ReleaseScopeUpdatePriorityAction releaseScopeUpdatePriorityAction = new ReleaseScopeUpdatePriorityAction(new UUID(), 1);
		set(releaseScopeUpdatePriorityAction, "releaseReferenceId", new UUID());
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
		scopeDeclareProgressAction.setTimestamp(new Date());
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

	@SuppressWarnings("unchecked")
	private static <T> T set(final Object subject, final String fieldName, final Object value) throws Exception {
		final Field field = subject.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(subject, value);
		field.setAccessible(false);
		return (T) subject;
	}
}
