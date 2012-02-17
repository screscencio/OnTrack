package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

class ScopeTreeInsertChildAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ScopeInsertAction action;

	public ScopeTreeInsertChildAction(final ScopeTreeWidget tree, final ScopeInsertAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context, final boolean isUserInteraction) throws ScopeNotFoundException {
		final Scope scope = context.findScope(action.getReferenceId());
		final Scope newScope = context.findScope(action.getNewScopeId());

		final ScopeTreeItem parentTreeItem = tree.findScopeTreeItem(scope);
		final ScopeTreeItem newItem = new ScopeTreeItem(newScope);

		parentTreeItem.insertItem(scope.getChildIndex(newScope), newItem);

		// TODO Is this necessary? The tree already receives a set of the modified scopes by the inference engines (effort, progress, ...).
		parentTreeItem.getScopeTreeItemWidget().updateDisplay();

		if (isUserInteraction) {
			parentTreeItem.setState(true);
			tree.setSelected(newItem);
		}
	}
}
