package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeNotFoundException;

class ScopeTreeMoveAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ScopeAction action;

	public ScopeTreeMoveAction(final ScopeTreeWidget tree, final ScopeAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute() throws ScopeNotFoundException {
		final Scope scope = action.getScope();
		final Scope parentScope = scope.getParent();
		final int index = parentScope.getChildIndex(scope);

		final ScopeTreeItem treeItem = tree.getScopeTreeItemFor(scope);
		final ScopeTreeItem parentItem = tree.getScopeTreeItemFor(parentScope);

		treeItem.remove();
		parentItem.insertItem(index, treeItem);
		openTreeHierarquyFor(treeItem);
		tree.setSelectedItem(treeItem);
	}

	@Override
	public void rollback() throws ScopeNotFoundException {
		execute();
	}

	private void openTreeHierarquyFor(final ScopeTreeItem treeItem) {
		ScopeTreeItem item = treeItem;
		while (item != null) {
			if (!item.getState()) item.setState(true);
			item = item.getParentItem();
		}
	}
}
