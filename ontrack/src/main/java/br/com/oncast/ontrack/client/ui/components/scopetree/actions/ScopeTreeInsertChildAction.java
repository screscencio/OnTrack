package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

class ScopeTreeInsertChildAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ScopeInsertAction action;

	public ScopeTreeInsertChildAction(final ScopeTreeWidget tree, final ScopeInsertAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context) throws ScopeNotFoundException {
		final Scope scope = context.findScope(action.getReferenceId());
		final Scope newScope = context.findScope(action.getNewScopeId());

		final ScopeTreeItem parentTreeItem = tree.findScopeTreeItem(scope.getId());
		final ScopeTreeItem newItem = new ScopeTreeItem(newScope);

		parentTreeItem.insertItem(scope.getChildIndex(newScope), newItem);

		parentTreeItem.setState(true);
		tree.setSelected(newItem);
	}

	@Override
	public void rollback(final ProjectContext context) throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = tree.findScopeTreeItem(action.getNewScopeId());

		final ScopeTreeItem parentItem = treeItem.getParentItem();
		final int childIndex = parentItem.getChildIndex(treeItem);
		parentItem.removeItem(treeItem);

		tree.setSelectedItem(((parentItem.getChildCount() > 0) ? parentItem.getChild((parentItem.getChildCount() - 1 < childIndex) ? parentItem.getChildCount() - 1
				: childIndex)
				: parentItem));
	}
}
