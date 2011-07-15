package br.com.oncast.ontrack.mocks.actions;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.mocks.models.ProjectMock;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertParentAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertSiblingDownAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertSiblingUpAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveDownAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveRightAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveUpAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeUpdateAction;

public class ActionMock {

	public static List<ModelAction> getActions() {
		final List<ModelAction> actions = new ArrayList<ModelAction>();

		final ScopeInsertChildAction insertChild1 = new ScopeInsertChildAction(ProjectMock.getProject().getProjectScope().getId(), "1");

		actions.add(insertChild1);
		actions.add(new ScopeInsertChildAction(ProjectMock.getProject().getProjectScope().getId(), "2"));
		actions.add(new ScopeInsertSiblingUpAction(insertChild1.getNewScopeId(), "Before 1"));
		actions.add(new ScopeInsertSiblingDownAction(insertChild1.getNewScopeId(), "After 1"));

		final ScopeInsertChildAction insertChild2 = new ScopeInsertChildAction(insertChild1.getNewScopeId(), "1.1");
		actions.add(insertChild2);
		actions.add(new ScopeInsertParentAction(insertChild2.getNewScopeId(), "Parent of 1.1"));

		final ScopeInsertChildAction insertChild3 = new ScopeInsertChildAction(ProjectMock.getProject().getProjectScope().getId(), "3");
		actions.add(insertChild3);
		actions.add(new ScopeMoveRightAction(insertChild3.getNewScopeId()));
		actions.add(new ScopeMoveLeftAction(insertChild3.getNewScopeId()));
		actions.add(new ScopeMoveDownAction(insertChild1.getNewScopeId()));
		actions.add(new ScopeMoveUpAction(insertChild1.getNewScopeId()));
		actions.add(new ScopeUpdateAction(insertChild1.getNewScopeId(), "new description"));

		return actions;
	}

	public static List<ModelAction> getActions2() {
		final List<ModelAction> actions = new ArrayList<ModelAction>();

		final ScopeInsertChildAction insertChild1 = new ScopeInsertChildAction(ProjectMock.getProject().getProjectScope().getId(), "a");

		actions.add(insertChild1);
		actions.add(new ScopeUpdateAction(insertChild1.getNewScopeId(), "new description for scope"));
		actions.add(new ScopeInsertChildAction(ProjectMock.getProject().getProjectScope().getId(), "b"));
		actions.add(new ScopeInsertSiblingUpAction(insertChild1.getNewScopeId(), "Before a"));
		actions.add(new ScopeInsertSiblingDownAction(insertChild1.getNewScopeId(), "After b"));
		actions.add(new ScopeMoveDownAction(insertChild1.getNewScopeId()));
		actions.add(new ScopeMoveUpAction(insertChild1.getNewScopeId()));

		final ScopeInsertChildAction insertChild2 = new ScopeInsertChildAction(insertChild1.getNewScopeId(), "1.1");
		actions.add(insertChild2);
		actions.add(new ScopeInsertParentAction(insertChild2.getNewScopeId(), "Parent of a.a"));

		final ScopeInsertChildAction insertChild3 = new ScopeInsertChildAction(ProjectMock.getProject().getProjectScope().getId(), "c");
		actions.add(insertChild3);
		actions.add(new ScopeMoveRightAction(insertChild3.getNewScopeId()));
		actions.add(new ScopeMoveLeftAction(insertChild3.getNewScopeId()));

		return actions;
	}
}
