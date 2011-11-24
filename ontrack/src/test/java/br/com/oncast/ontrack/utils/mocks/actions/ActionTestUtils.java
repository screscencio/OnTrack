package br.com.oncast.ontrack.utils.mocks.actions;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertParentAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertSiblingDownAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertSiblingUpAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeMoveDownAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeMoveRightAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeMoveUpAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbolsProvider;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

public class ActionTestUtils {

	public static List<ModelAction> getSomeActions() {
		final List<ModelAction> actions = new ArrayList<ModelAction>();

		final ScopeInsertChildAction insertChild1 = new ScopeInsertChildAction(ProjectTestUtils.createProject().getProjectScope().getId(), "1");

		actions.add(insertChild1);
		actions.add(new ScopeInsertChildAction(ProjectTestUtils.createProject().getProjectScope().getId(), "2"));
		actions.add(new ScopeInsertSiblingUpAction(insertChild1.getNewScopeId(), "Before 1"));
		actions.add(new ScopeInsertSiblingDownAction(insertChild1.getNewScopeId(), "After 1"));

		final ScopeInsertChildAction insertChild2 = new ScopeInsertChildAction(insertChild1.getNewScopeId(), "1.1");
		actions.add(insertChild2);
		actions.add(new ScopeInsertParentAction(insertChild2.getNewScopeId(), "Parent of 1.1"));

		final ScopeInsertChildAction insertChild3 = new ScopeInsertChildAction(ProjectTestUtils.createProject().getProjectScope().getId(), "3");
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
}
