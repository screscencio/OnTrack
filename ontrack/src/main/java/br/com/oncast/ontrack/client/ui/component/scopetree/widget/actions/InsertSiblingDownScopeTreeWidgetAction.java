package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.NotFoundException;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.beans.Scope;

public class InsertSiblingDownScopeTreeWidgetAction implements ScopeTreeWidgetAction {

	private final ScopeTreeWidget tree;
	private final ScopeAction action;

	public InsertSiblingDownScopeTreeWidgetAction(final ScopeTreeWidget tree, final ScopeAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		final Scope newScope = action.getScope();
		if (newScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to create a sibling for a root node.");

		final ScopeTreeItem parentTreeItem = getTreeItemFor(tree, newScope.getParent());
		final ScopeTreeItem newItem = new ScopeTreeItem(newScope);
		parentTreeItem.insertItem(newScope.getIndex(), newItem);
		newItem.enterEditMode();
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		new RemoveScopeTreeWidgetAction(tree, action).execute();
	}

	private ScopeTreeItem getTreeItemFor(final ScopeTreeWidget tree, final Scope scope) throws UnableToCompleteActionException {
		ScopeTreeItem treeItem;
		try {
			treeItem = tree.getScopeTreeItemFor(scope);
		}
		catch (final NotFoundException e) {
			throw new UnableToCompleteActionException("Tree item could not be found.", e);
		}
		return treeItem;
	}
}
