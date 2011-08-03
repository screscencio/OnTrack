package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

class ScopeTreeMoveAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ScopeAction action;

	public ScopeTreeMoveAction(final ScopeTreeWidget tree, final ScopeAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context) throws ScopeNotFoundException {
		final Scope scope = context.findScope(action.getReferenceId());
		final Scope parentScope = scope.getParent();
		final int index = parentScope.getChildIndex(scope);

		final ScopeTreeItem treeItem = tree.findScopeTreeItem(scope.getId());
		final ScopeTreeItem parentItem = tree.findScopeTreeItem(parentScope.getId());

		// TODO Refactor the update of display of modified items. Maybe the algorithm should depend on the runtime action instance.
		if (!treeItem.isRoot()) treeItem.getParentItem().getScopeTreeItemWidget().updateDisplay();
		treeItem.remove();
		parentItem.insertItem(index, treeItem);
		treeItem.setHierarchicalState(true);
		tree.setSelectedItem(treeItem);

		treeItem.getScopeTreeItemWidget().updateDisplay();
		parentItem.getScopeTreeItemWidget().updateDisplay();
	}
}
