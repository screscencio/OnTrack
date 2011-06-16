package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertAction;
import br.com.oncast.ontrack.shared.scope.exceptions.ScopeNotFoundException;

class ScopeTreeInsertAsFatherAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ScopeInsertAction action;

	public ScopeTreeInsertAsFatherAction(final ScopeTreeWidget tree, final ScopeInsertAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context) throws ScopeNotFoundException {
		final Scope scope = context.findScope(action.getReferenceId());
		final Scope newScope = context.findScope(action.getNewScopeId());
		final Scope grandParentScope = newScope.getParent();

		final ScopeTreeItem treeItem = tree.getScopeTreeItemFor(scope.getId());
		final ScopeTreeItem grandParentTreeItem = tree.getScopeTreeItemFor(grandParentScope.getId());
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
	public void rollback(final ProjectContext context) throws ScopeNotFoundException {
		final Scope scope = context.findScope(action.getReferenceId());
		final Scope grandParentScope = scope.getParent();

		final ScopeTreeItem treeItem = tree.getScopeTreeItemFor(scope.getId());
		final ScopeTreeItem grandParentTreeItem = tree.getScopeTreeItemFor(grandParentScope.getId());

		final int index = grandParentScope.getChildIndex(scope);
		grandParentTreeItem.removeItem(treeItem.getParentItem());
		grandParentTreeItem.insertItem(index, treeItem);

		grandParentTreeItem.setState(true);
		tree.setSelected(treeItem);
	}
}
