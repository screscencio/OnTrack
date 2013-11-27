package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

class ScopeTreeInsertSiblingAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ScopeInsertAction action;

	public ScopeTreeInsertSiblingAction(final ScopeTreeWidget tree, final ScopeInsertAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context, ActionContext actionContext, final boolean isUserInteraction) throws ScopeNotFoundException {
		final Scope newScope = context.findScope(action.getNewScopeId());
		final Scope parent = newScope.getParent();

		final ScopeTreeItem parentItem = tree.findScopeTreeItem(parent);
		if (parentItem.isFake()) return;

		final ScopeTreeItem newItem = new ScopeTreeItem(newScope);
		parentItem.insertItem(parent.getChildIndex(newScope), newItem);
		if (isUserInteraction) tree.setSelectedItem(newItem, true);
	}
}
