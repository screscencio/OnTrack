package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

class ScopeTreeMoveAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ScopeAction action;

	public ScopeTreeMoveAction(final ScopeTreeWidget tree, final ScopeAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context, final ActionContext actionContext, final boolean isUserInteraction) throws ScopeNotFoundException {
		final Scope scope = context.findScope(action.getReferenceId());
		final Scope parentScope = scope.getParent();
		final int index = parentScope.getChildIndex(scope);

		ScopeTreeItem treeItem = tree.findScopeTreeItem(scope);
		treeItem.remove();
		if (treeItem.isFake()) treeItem = new ScopeTreeItem(scope);
		final ScopeTreeItem parentItem = tree.findScopeTreeItem(parentScope);
		parentItem.insertItem(index, treeItem);

		if (!isUserInteraction) return;

		treeItem.setHierarchicalState(true);
		tree.setSelectedItem(treeItem, true);

	}
}
