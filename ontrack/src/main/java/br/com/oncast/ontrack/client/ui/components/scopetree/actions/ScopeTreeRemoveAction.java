package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

class ScopeTreeRemoveAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ScopeAction action;

	public ScopeTreeRemoveAction(final ScopeTreeWidget tree, final ScopeAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context) throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = tree.findScopeTreeItem(action.getReferenceId());

		final ScopeTreeItem parentItem = treeItem.getParentItem();
		final int childIndex = parentItem.getChildIndex(treeItem);
		parentItem.removeItem(treeItem);

		tree.setSelectedItem(((parentItem.getChildCount() > 0) ? parentItem.getChild((parentItem.getChildCount() - 1 < childIndex) ? parentItem.getChildCount() - 1
				: childIndex)
				: parentItem));
	}

	@Override
	public void rollback(final ProjectContext context) throws ScopeNotFoundException {
		final Scope referencedScope = context.findScope(action.getReferenceId());
		final Scope parentScope = referencedScope.getParent();

		final int childIndex = parentScope.getChildIndex(referencedScope);

		final ScopeTreeItem parentItem = tree.findScopeTreeItem(parentScope.getId());
		final ScopeTreeItem newTreeItem = new ScopeTreeItem(referencedScope);
		parentItem.insertItem(childIndex, newTreeItem);

		tree.setSelected(newTreeItem);
	}
}
