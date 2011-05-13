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

		TreeItem item = newItem;
		while (item != null) {
			if (!item.getState()) item.setState(true);
			item = item.getParentItem();
		}

		newItem.enterEditMode();
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		final ScopeTreeItem newItem = (ScopeTreeItem) childTreeItem.getParentItem();
		if (newItem.isRoot()) throw new UnableToCompleteActionException("The action cannot be undone because an inconsistence was found.");
		childTreeItem.remove();

		final TreeItem parentItem = newItem.getParentItem();
		final int childIndex = parentItem.getChildIndex(newItem);
		parentItem.removeItem(newItem);

		parentItem.insertItem(childIndex, childTreeItem);
	}
}
