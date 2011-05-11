package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.shared.beans.Scope;

public class InsertSiblingDownScopeTreeWidgetAction implements ScopeTreeWidgetAction {

	private final ScopeTreeItem parentTreeItem;
	private final Scope referencedScope;

	public InsertSiblingDownScopeTreeWidgetAction(final ScopeTreeItem parentTreeItem, final Scope referencedScope) {
		this.parentTreeItem = parentTreeItem;
		this.referencedScope = referencedScope;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		if (parentTreeItem.isRoot()) throw new UnableToCompleteActionException("It is not possible to create a sibling for a root node.");

		final ScopeTreeItem newItem = new ScopeTreeItem(referencedScope);
		parentTreeItem.getParentItem().insertItem(parentTreeItem.getParentItem().getChildIndex(parentTreeItem) + 1, newItem);
		newItem.enterEditMode();
	}
}
