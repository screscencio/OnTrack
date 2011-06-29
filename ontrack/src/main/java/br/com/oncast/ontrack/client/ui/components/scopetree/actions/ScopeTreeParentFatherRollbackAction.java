package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

public class ScopeTreeParentFatherRollbackAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ScopeAction action;

	public ScopeTreeParentFatherRollbackAction(final ScopeTreeWidget tree, final ScopeAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context) throws ScopeNotFoundException {
		final Scope scope = context.findScope(action.getReferenceId());
		final Scope grandParentScope = scope.getParent();

		final ScopeTreeItem treeItem = tree.findScopeTreeItem(scope.getId());
		final ScopeTreeItem grandParentTreeItem = tree.findScopeTreeItem(grandParentScope.getId());

		final int index = grandParentScope.getChildIndex(scope);
		grandParentTreeItem.removeItem(treeItem.getParentItem());
		grandParentTreeItem.insertItem(index, treeItem);

		grandParentTreeItem.setHierarchicalState(true);
		tree.setSelected(treeItem);
	}
}
