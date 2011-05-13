package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.shared.beans.Scope;

import com.google.gwt.user.client.ui.TreeItem;

public class InsertSiblingUpScopeTreeWidgetAction implements ScopeTreeWidgetAction {

	private final ScopeTreeItem treeItem;
	private final Scope referencedScope;

	public InsertSiblingUpScopeTreeWidgetAction(final ScopeTreeItem treeItem, final Scope referencedScope) {
		this.treeItem = treeItem;
		this.referencedScope = referencedScope;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		if (treeItem.isRoot()) throw new UnableToCompleteActionException("It is not possible to create a sibling for a root node.");
		final ScopeTreeItem newItem = new ScopeTreeItem(referencedScope);
		treeItem.getParentItem().insertItem(treeItem.getParentItem().getChildIndex(treeItem), newItem);
		newItem.enterEditMode();
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		if (treeItem.isRoot()) throw new UnableToCompleteActionException("The action cannot be undone because an inconsistence was found.");
		final TreeItem parentItem = treeItem.getParentItem();
		final int index = parentItem.getChildIndex(treeItem) - 1;

		if (index < 0) throw new UnableToCompleteActionException("The action cannot be undone because an inconsistence was found.");

		new RemoveScopeTreeWidgetAction((ScopeTreeItem) parentItem.getChild(index)).execute();
	}
}
