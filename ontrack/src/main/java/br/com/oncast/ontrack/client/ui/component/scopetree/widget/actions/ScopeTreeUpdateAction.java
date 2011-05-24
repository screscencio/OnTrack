package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeNotFoundException;

class ScopeTreeUpdateAction implements ScopeTreeAction {

	private final Scope scope;
	private final ScopeTreeWidget tree;

	public ScopeTreeUpdateAction(final ScopeTreeWidget tree, final ScopeAction action) {
		this.tree = tree;
		this.scope = action.getScope();
	}

	@Override
	public void execute() throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = tree.getScopeTreeItemFor(scope);
		treeItem.setDescription(scope.getDescription());
		treeItem.getTree().setSelectedItem(treeItem);
	}

	@Override
	public void rollback() throws ScopeNotFoundException {
		execute();
	}
}
