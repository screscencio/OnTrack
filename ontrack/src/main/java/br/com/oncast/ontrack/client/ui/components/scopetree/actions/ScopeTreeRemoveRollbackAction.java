package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

public class ScopeTreeRemoveRollbackAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ScopeInsertAction action;

	public ScopeTreeRemoveRollbackAction(final ScopeTreeWidget tree, final ScopeInsertAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context, final boolean isUserInteraction) throws ScopeNotFoundException {
		final Scope referencedScope = context.findScope(action.getNewScopeId());
		final Scope parentScope = referencedScope.getParent();

		final int childIndex = parentScope.getChildIndex(referencedScope);

		final ScopeTreeItem parentItem = tree.findScopeTreeItem(parentScope);
		final ScopeTreeItem newTreeItem = new ScopeTreeItem(referencedScope);
		newTreeItem.mountTwoLevels();
		parentItem.insertItem(childIndex, newTreeItem);

		// TODO Is this necessary? The tree already receives a set of the modified scopes by the inference engines (effort, progress, ...).
		parentItem.getScopeTreeItemWidget().updateDisplay();

		if (isUserInteraction) {
			newTreeItem.setHierarchicalState(true);
			tree.setSelectedItem(newTreeItem);
		}
	}
}
