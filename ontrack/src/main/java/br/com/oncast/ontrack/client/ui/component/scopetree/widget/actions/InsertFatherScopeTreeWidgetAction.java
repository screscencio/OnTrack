package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.shared.beans.Scope;

import com.google.gwt.user.client.ui.TreeItem;

public class InsertFatherScopeTreeWidgetAction implements ScopeTreeWidgetAction {

	private final ScopeTreeItem childTreeItem;
	private final Scope referencedScope;

	public InsertFatherScopeTreeWidgetAction(final ScopeTreeItem childTreeItem, final Scope referencedScope) {
		this.childTreeItem = childTreeItem;
		this.referencedScope = referencedScope;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		if (childTreeItem.isRoot()) throw new UnableToCompleteActionException("It is not possible to create a parent node for a root node.");

		final TreeItem parentItem = childTreeItem.getParentItem();
		final int index = parentItem.getChildIndex(childTreeItem);
		parentItem.removeItem(childTreeItem);

		final ScopeTreeItem newItem = new ScopeTreeItem(referencedScope);
		parentItem.insertItem(index, newItem);

		if (!newItem.getState()) newItem.setState(true);
		newItem.enterEditMode();
	}
}
