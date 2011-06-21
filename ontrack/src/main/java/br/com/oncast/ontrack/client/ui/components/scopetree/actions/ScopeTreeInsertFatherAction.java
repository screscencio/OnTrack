package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

class ScopeTreeInsertFatherAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ScopeInsertAction action;

	public ScopeTreeInsertFatherAction(final ScopeTreeWidget tree, final ScopeInsertAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context) throws ScopeNotFoundException {
		final Scope scope = context.findScope(action.getReferenceId());
		final Scope newScope = context.findScope(action.getNewScopeId());
		final Scope grandParentScope = newScope.getParent();

		final ScopeTreeItem treeItem = tree.findScopeTreeItem(scope.getId());
		final ScopeTreeItem grandParentTreeItem = tree.findScopeTreeItem(grandParentScope.getId());
		final ScopeTreeItem newTreeItem = new ScopeTreeItem(newScope);

		final int index = grandParentScope.getChildIndex(newScope);
		grandParentTreeItem.removeItem(treeItem);
		grandParentTreeItem.insertItem(index, newTreeItem);

		newTreeItem.setHierarchicalState(true);
		tree.setSelected(newTreeItem);
	}

	@Override
	public void rollback(final ProjectContext context) throws ScopeNotFoundException {
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
