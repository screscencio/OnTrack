package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertParentAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertParentRollbackAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertSiblingAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeRemoveRollbackAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

// TODO ++Refactor this class to decentralize Action to WidgetActionFactory mappings.
public class ScopeTreeActionFactory {

	private final ScopeTreeWidget tree;

	public ScopeTreeActionFactory(final ScopeTreeWidget tree) {
		this.tree = tree;
	}

	public ScopeTreeAction createEquivalentActionFor(final ModelAction action) throws ScopeNotFoundException {

		if (action instanceof ScopeRemoveAction) return new ScopeTreeRemoveAction(tree, (ScopeAction) action);
		else if (action instanceof ScopeMoveAction) return new ScopeTreeMoveAction(tree, (ScopeAction) action);
		else if (action instanceof ScopeInsertSiblingAction) return new ScopeTreeInsertSiblingAction(tree, (ScopeInsertAction) action);
		else if (action instanceof ScopeInsertChildAction) return new ScopeTreeInsertChildAction(tree, (ScopeInsertAction) action);
		else if (action instanceof ScopeInsertParentAction) return new ScopeTreeInsertParentAction(tree, (ScopeInsertAction) action);
		else if (action instanceof ScopeInsertParentRollbackAction) return new ScopeTreeParentFatherRollbackAction(tree, (ScopeAction) action);
		else if (action instanceof ScopeRemoveRollbackAction) return new ScopeTreeRemoveRollbackAction(tree, (ScopeAction) action);
		else if (action instanceof ScopeUpdateAction) return new ScopeTreeUpdateAction(tree, (ScopeAction) action);

		throw new ScopeNotFoundException("It was not possible to find the desired action.");
	}
}
