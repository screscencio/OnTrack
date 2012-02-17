package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

class ScopeTreeUpdateAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ScopeAction action;

	public ScopeTreeUpdateAction(final ScopeTreeWidget tree, final ScopeAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context, final boolean isUserInteraction) throws ScopeNotFoundException {
		// TODO ++Use treeItem.update(scope) instead of treeItem.setReferencedScope(scope).
		Scope scope = context.findScope(action.getReferenceId());
		
		final ScopeTreeItem treeItem = tree.findScopeTreeItem(scope);
		treeItem.setReferencedScope(scope);

		if (isUserInteraction) treeItem.getTree().setSelectedItem(treeItem);
	}
}
