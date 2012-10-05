package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

class ScopeTreeInsertParentAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ScopeInsertAction action;

	public ScopeTreeInsertParentAction(final ScopeTreeWidget tree, final ScopeInsertAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context, final ActionContext actionContext, final boolean isUserInteraction) throws ScopeNotFoundException {
		final Scope scope = context.findScope(action.getReferenceId());
		final Scope newScope = context.findScope(action.getNewScopeId());
		final Scope grandParentScope = newScope.getParent();

		final ScopeTreeItem treeItem = tree.findScopeTreeItem(scope);
		final ScopeTreeItem grandParentTreeItem = tree.findScopeTreeItem(grandParentScope);
		final ScopeTreeItem newTreeItem = new ScopeTreeItem(newScope);

		final int index = grandParentScope.getChildIndex(newScope);
		grandParentTreeItem.removeItem(treeItem);
		grandParentTreeItem.insertItem(index, newTreeItem);
		newTreeItem.addItem(treeItem);

		if (isUserInteraction) {
			newTreeItem.setHierarchicalState(true);
			newTreeItem.setState(true);
			tree.setSelectedItem(newTreeItem, true);
		}
	}

}
