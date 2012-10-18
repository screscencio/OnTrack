package br.com.oncast.ontrack.utils.mocks.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.mockito.Mockito;
import org.reflections.Reflections;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertParentAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingDownAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingUpAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveDownAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveRightAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveUpAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbolsProvider;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class ActionTestUtils {

	public static List<ModelAction> createSomeActions(final User... requiredUsers) {
		final List<ModelAction> actions = new ArrayList<ModelAction>();

		for (final User user : requiredUsers) {
			if (user != null) actions.add(new TeamInviteAction(user));
		}

		final UUID rootScope = ProjectTestUtils.createProject().getProjectScope().getId();
		final ScopeInsertChildAction insertChild1 = new ScopeInsertChildAction(rootScope, "1");
		actions.add(insertChild1);
		actions.add(new ScopeInsertChildAction(rootScope, "2"));
		actions.add(new ScopeInsertSiblingUpAction(insertChild1.getNewScopeId(), "Before 1"));
		actions.add(new ScopeInsertSiblingDownAction(insertChild1.getNewScopeId(), "After 1"));

		final ScopeInsertChildAction insertChild2 = new ScopeInsertChildAction(insertChild1.getNewScopeId(), "1.1");
		actions.add(insertChild2);
		actions.add(new ScopeInsertParentAction(insertChild2.getNewScopeId(), "Parent of 1.1"));

		final ScopeInsertChildAction insertChild3 = new ScopeInsertChildAction(rootScope, "3");
		actions.add(insertChild3);
		actions.add(new ScopeMoveRightAction(insertChild3.getNewScopeId()));
		actions.add(new ScopeMoveLeftAction(insertChild3.getNewScopeId()));
		actions.add(new ScopeMoveDownAction(insertChild1.getNewScopeId()));
		actions.add(new ScopeMoveUpAction(insertChild1.getNewScopeId()));
		actions.add(new ScopeUpdateAction(insertChild1.getNewScopeId(), "new description"));

		actions.add(new ScopeUpdateAction(insertChild2.getNewScopeId(), "new description " + StringRepresentationSymbolsProvider.RELEASE_SYMBOL + "R1"));
		final ScopeInsertChildAction insertChild31 = new ScopeInsertChildAction(insertChild3.getNewScopeId(),
				"3.1 " + StringRepresentationSymbolsProvider.RELEASE_SYMBOL + "R1/It1");
		actions.add(insertChild31);
		actions.add(new ScopeInsertSiblingUpAction(insertChild31.getNewScopeId(),
				"Before 3.1 " + StringRepresentationSymbolsProvider.RELEASE_SYMBOL + "R2/It1"));
		actions.add(new ScopeInsertSiblingDownAction(insertChild31.getNewScopeId(),
				"After 3.1 " + StringRepresentationSymbolsProvider.RELEASE_SYMBOL + "R2/It1"));
		actions.add(new ScopeInsertParentAction(insertChild3.getNewScopeId(),
				"Parent of 3 " + StringRepresentationSymbolsProvider.RELEASE_SYMBOL + "R1"));

		return actions;
	}

	public static List<ModelAction> getActions2() {
		final List<ModelAction> actions = new ArrayList<ModelAction>();

		final ScopeInsertChildAction insertChild1 = new ScopeInsertChildAction(ProjectTestUtils.createProject().getProjectScope().getId(), "a");

		actions.add(insertChild1);
		actions.add(new ScopeUpdateAction(insertChild1.getNewScopeId(), "new description for scope"));
		actions.add(new ScopeInsertChildAction(ProjectTestUtils.createProject().getProjectScope().getId(), "b"));
		actions.add(new ScopeInsertSiblingUpAction(insertChild1.getNewScopeId(), "Before a"));
		actions.add(new ScopeInsertSiblingDownAction(insertChild1.getNewScopeId(), "After b"));
		actions.add(new ScopeMoveDownAction(insertChild1.getNewScopeId()));
		actions.add(new ScopeMoveUpAction(insertChild1.getNewScopeId()));

		final ScopeInsertChildAction insertChild2 = new ScopeInsertChildAction(insertChild1.getNewScopeId(), "1.1");
		actions.add(insertChild2);
		actions.add(new ScopeInsertParentAction(insertChild2.getNewScopeId(), "Parent of a.a"));

		final ScopeInsertChildAction insertChild3 = new ScopeInsertChildAction(ProjectTestUtils.createProject().getProjectScope().getId(), "c");
		actions.add(insertChild3);
		actions.add(new ScopeMoveRightAction(insertChild3.getNewScopeId()));
		actions.add(new ScopeMoveLeftAction(insertChild3.getNewScopeId()));

		return actions;
	}

	public static List<ModelAction> createOneValidAction() {
		final List<ModelAction> actions = new ArrayList<ModelAction>();
		actions.add(new TeamInviteAction(UserTestUtils.getAdmin()));
		return actions;
	}

	public static void assertExpectedKanbanColumns(final ProjectContext context, final Release release, final int expectedColumns,
			final String... columnDescriptions) {
		final Kanban kanban = context.getKanban(release);
		Assert.assertEquals("Its should have " + expectedColumns + " columns.", expectedColumns, kanban.getColumns().size());

		int i = 0;
		for (final String column : columnDescriptions) {
			Assert.assertNotNull("It should have a column for " + column + ".", kanban.getColumn(column));
			if (kanban.isStaticColumn(column)) continue;
			assertExpectedKanbanColumnPosition(context, release, column, i++);
		}
	}

	public static void assertExpectedKanbanColumnPosition(final ProjectContext context, final Release release, final String columnDescription,
			final int columnPosition) {
		Assert.assertEquals("The position for the column '" + columnDescription + "' should be '" + columnPosition + "'.", columnPosition,
				context.getKanban(release).indexOf(columnDescription));
	}

	public static void assertProgressForScopes(final String expectedProgressDescription, final Scope... scopes) {
		for (final Scope scope : scopes) {
			final String desc = scope.getProgress().getDescription().isEmpty() ? Progress.DEFAULT_NOT_STARTED_NAME : scope.getProgress().getDescription();
			Assert.assertEquals("The progress set for the scope '" + scope.getDescription() + "' is not '" + expectedProgressDescription + "'.",
					expectedProgressDescription,
					desc);
		}
	}

	public static <T extends ModelAction> List<T> getAllSubactionsOfType(final Class<T> clazz) {
		final ArrayList<T> actions = new ArrayList<T>();
		final Set<Class<? extends T>> subTypes = new Reflections(ModelAction.class.getPackage().getName()).getSubTypesOf(clazz);
		for (final Class<? extends T> sub : subTypes) {
			actions.add(Mockito.mock(sub));
		}
		return actions;
	}

	public static ModelAction execute(final ModelAction action, final ProjectContext context) throws UnableToCompleteActionException {
		return action.execute(context, Mockito.mock(ActionContext.class));
	}
}
