package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.scope.exceptions.ScopeNotFoundException;

class ScopeTreeUpdateAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ScopeAction action;

	public ScopeTreeUpdateAction(final ScopeTreeWidget tree, final ScopeAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context) throws ScopeNotFoundException {
		final ScopeTreeItem treeItem = tree.getScopeTreeItemFor(action.getReferenceId());
		treeItem.setReferencedScope(context.findScope(action.getReferenceId()));
		treeItem.getTree().setSelectedItem(treeItem);
	}

	@Override
	public void rollback(final ProjectContext context) throws ScopeNotFoundException {
		execute(context);
	}
}
