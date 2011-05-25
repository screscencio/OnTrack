package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeNotFoundException;

class ScopeTreeInsertAsFatherAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ScopeInsertAction action;

	public ScopeTreeInsertAsFatherAction(final ScopeTreeWidget tree, final ScopeInsertAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute() throws ScopeNotFoundException {
		final Scope scope = action.getScope();
		final Scope newScope = action.getNewScope();
		final Scope grandParentScope = newScope.getParent();

		final ScopeTreeItem treeItem = tree.getScopeTreeItemFor(scope);
		final ScopeTreeItem grandParentTreeItem = tree.getScopeTreeItemFor(grandParentScope);
		final ScopeTreeItem newTreeItem = new ScopeTreeItem(newScope);

		final int index = grandParentScope.getChildIndex(newScope);
		grandParentTreeItem.removeItem(treeItem);
		grandParentTreeItem.insertItem(index, newTreeItem);

		openHierarquyFor(newTreeItem);

		grandParentTreeItem.setState(true);
		tree.setSelected(newTreeItem);
		newTreeItem.enterEditMode();
	}

	private void openHierarquyFor(final ScopeTreeItem newTreeItem) {
		ScopeTreeItem item = newTreeItem;
		while (item != null) {
			if (!item.getState()) item.setState(true);
			item = item.getParentItem();
		}
	}

	@Override
	public void rollback() throws ScopeNotFoundException {
		final Scope scope = action.getScope();
		final Scope newScope = action.getNewScope();
		final Scope grandParentScope = scope.getParent();

		final ScopeTreeItem treeItem = tree.getScopeTreeItemFor(scope);
		final ScopeTreeItem grandParentTreeItem = tree.getScopeTreeItemFor(grandParentScope);
		final ScopeTreeItem newTreeItem = tree.getScopeTreeItemFor(newScope);

		final int index = grandParentScope.getChildIndex(scope);
		grandParentTreeItem.removeItem(newTreeItem);
		grandParentTreeItem.insertItem(index, treeItem);

		grandParentTreeItem.setState(true);
		tree.setSelected(treeItem);
	}
}
