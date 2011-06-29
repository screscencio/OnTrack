package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

public class ScopeTreeRemoveRollbackAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ScopeAction action;

	public ScopeTreeRemoveRollbackAction(final ScopeTreeWidget tree, final ScopeAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context) throws ScopeNotFoundException {
		final Scope referencedScope = context.findScope(action.getReferenceId());
		final Scope parentScope = referencedScope.getParent();

		final int childIndex = parentScope.getChildIndex(referencedScope);

		final ScopeTreeItem parentItem = tree.findScopeTreeItem(parentScope.getId());
		final ScopeTreeItem newTreeItem = new ScopeTreeItem(referencedScope);
		parentItem.insertItem(childIndex, newTreeItem);

		newTreeItem.setHierarchicalState(true);
		tree.setSelected(newTreeItem);
	}
}
