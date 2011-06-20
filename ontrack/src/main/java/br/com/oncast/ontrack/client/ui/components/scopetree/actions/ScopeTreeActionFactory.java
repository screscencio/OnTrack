package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertAsFatherAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertSiblingAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

// TODO Refactor this class to decentralize Action to WidgetActionFactory mappings.
public class ScopeTreeActionFactory {

	private final ScopeTreeWidget tree;

	public ScopeTreeActionFactory(final ScopeTreeWidget tree) {
		this.tree = tree;
	}

	public ScopeTreeAction createEquivalentActionFor(final ModelAction action) throws ScopeNotFoundException {

		if (action instanceof ScopeRemoveAction) return new ScopeTreeRemoveAction(tree, (ScopeRemoveAction) action);
		if (action instanceof ScopeMoveAction) return new ScopeTreeMoveAction(tree, (ScopeMoveAction) action);
		if (action instanceof ScopeUpdateAction) return new ScopeTreeUpdateAction(tree, (ScopeUpdateAction) action);
		if (action instanceof ScopeInsertSiblingAction) return new ScopeTreeInsertSiblingAction(tree, (ScopeInsertAction) action);

		if (action instanceof ScopeInsertChildAction) return new ScopeTreeInsertChildAction(tree, (ScopeInsertAction) action);
		if (action instanceof ScopeInsertAsFatherAction) return new ScopeTreeInsertAsFatherAction(tree, (ScopeInsertAction) action);

		throw new ScopeNotFoundException("It was not possible to find the desired action.");
	}
}
