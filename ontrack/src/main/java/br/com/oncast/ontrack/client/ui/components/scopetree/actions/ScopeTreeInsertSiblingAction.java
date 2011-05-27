package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeNotFoundException;

class ScopeTreeInsertSiblingAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ScopeInsertAction action;

	public ScopeTreeInsertSiblingAction(final ScopeTreeWidget tree, final ScopeInsertAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute() throws ScopeNotFoundException {
		final Scope newScope = action.getNewScope();
		final Scope parentScope = action.getNewScope().getParent();

		final ScopeTreeItem parentTreeItem = tree.getScopeTreeItemFor(parentScope);
		final ScopeTreeItem newItem = new ScopeTreeItem(newScope);

		parentTreeItem.insertItem(parentScope.getChildIndex(newScope), newItem);
		tree.setSelected(newItem);
		newItem.enterEditMode();
	}

	@Override
	public void rollback() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = tree.getScopeTreeItemFor(action.getNewScope());

		final ScopeTreeItem parentItem = treeItem.getParentItem();
		final int childIndex = parentItem.getChildIndex(treeItem);
		parentItem.removeItem(treeItem);

		tree.setSelectedItem(((parentItem.getChildCount() > 0) ? parentItem.getChild((parentItem.getChildCount() - 1 < childIndex) ? parentItem.getChildCount() - 1
				: childIndex)
				: parentItem));
	}
}
