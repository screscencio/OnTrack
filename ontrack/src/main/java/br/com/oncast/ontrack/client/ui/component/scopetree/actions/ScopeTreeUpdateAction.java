package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.widgets.ScopeTreeWidget;
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
		treeItem.setReferencedScope(scope);
		treeItem.getTree().setSelectedItem(treeItem);
	}

	@Override
	public void rollback() throws ScopeNotFoundException {
		execute();
	}
}
