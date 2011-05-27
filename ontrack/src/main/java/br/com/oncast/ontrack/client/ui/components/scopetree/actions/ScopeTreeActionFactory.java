package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertAsFatherAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertSiblingAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeMoveAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.scope.actions.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeUpdateAction;

// TODO Refactor this class to decentralize Action to WidgetActionFactory mappings.
public class ScopeTreeActionFactory {

	private final ScopeTreeWidget tree;

	public ScopeTreeActionFactory(final ScopeTreeWidget tree) {
		this.tree = tree;
	}

	public ScopeTreeAction createEquivalentActionFor(final ScopeAction action) throws ScopeNotFoundException {

		if (action instanceof ScopeRemoveAction) return new ScopeTreeRemoveAction(tree, action);
		if (action instanceof ScopeMoveAction) return new ScopeTreeMoveAction(tree, action);
		if (action instanceof ScopeUpdateAction) return new ScopeTreeUpdateAction(tree, action);
		if (action instanceof ScopeInsertSiblingAction) return new ScopeTreeInsertSiblingAction(tree, (ScopeInsertAction) action);

		if (action instanceof ScopeInsertChildAction) return new ScopeTreeInsertChildAction(tree, (ScopeInsertAction) action);
		if (action instanceof ScopeInsertAsFatherAction) return new ScopeTreeInsertAsFatherAction(tree, (ScopeInsertAction) action);

		throw new ScopeNotFoundException("It was not possible to find the desired action.");
	}
}
