package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.scope.exceptions.ScopeNotFoundException;

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

		final ScopeTreeItem treeItem = tree.getScopeTreeItemFor(scope.getId());
		final ScopeTreeItem parentItem = tree.getScopeTreeItemFor(parentScope.getId());

		treeItem.remove();
		parentItem.insertItem(index, treeItem);
		openTreeHierarquyFor(treeItem);
		tree.setSelectedItem(treeItem);
	}

	@Override
	public void rollback(final ProjectContext context) throws ScopeNotFoundException {
		execute(context);
	}

	private void openTreeHierarquyFor(final ScopeTreeItem treeItem) {
		ScopeTreeItem item = treeItem;
		while (item != null) {
			if (!item.getState()) item.setState(true);
			item = item.getParentItem();
		}
	}
}
