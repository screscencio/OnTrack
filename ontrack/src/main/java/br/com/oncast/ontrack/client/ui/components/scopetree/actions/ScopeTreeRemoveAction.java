package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeNotFoundException;

class ScopeTreeRemoveAction implements ScopeTreeAction {

	private final Scope referencedScope;
	private final ScopeTreeWidget tree;

	public ScopeTreeRemoveAction(final ScopeTreeWidget tree, final ScopeAction action) {
		this.tree = tree;
		this.referencedScope = action.getScope();
	}

	@Override
	public void execute() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = tree.getScopeTreeItemFor(referencedScope);

		final ScopeTreeItem parentItem = treeItem.getParentItem();
		final int childIndex = parentItem.getChildIndex(treeItem);
		parentItem.removeItem(treeItem);

		tree.setSelectedItem(((parentItem.getChildCount() > 0) ? parentItem.getChild((parentItem.getChildCount() - 1 < childIndex) ? parentItem.getChildCount() - 1
				: childIndex)
				: parentItem));
	}

	@Override
	public void rollback() throws ScopeNotFoundException {
		final Scope parentScope = referencedScope.getParent();
		final int childIndex = parentScope.getChildIndex(referencedScope);
		final ScopeTreeItem parentItem = tree.getScopeTreeItemFor(parentScope);
		final ScopeTreeItem newTreeItem = new ScopeTreeItem(referencedScope);
		parentItem.insertItem(childIndex, newTreeItem);
		tree.setSelected(newTreeItem);
	}
}
