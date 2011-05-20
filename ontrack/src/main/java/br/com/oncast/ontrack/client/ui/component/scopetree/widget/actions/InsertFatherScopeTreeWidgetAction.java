package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.NotFoundException;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.beans.Scope;

import com.google.gwt.user.client.ui.TreeItem;

public class InsertFatherScopeTreeWidgetAction implements ScopeTreeWidgetAction {

	private final ScopeTreeWidget tree;
	private final ScopeAction action;

	public InsertFatherScopeTreeWidgetAction(final ScopeTreeWidget tree, final ScopeAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		final Scope newItem = action.getScope();

		final TreeItem oldParent = getTreeItemFor(tree, newItem.getChildren().get(0));
		final int index = oldParent.getParentItem().getChildIndex(oldParent);

		final TreeItem newParent = oldParent.getParentItem();
		newParent.removeItem(oldParent);

		final ScopeTreeItem newTreeItem = new ScopeTreeItem(newItem);
		newParent.insertItem(index, newTreeItem);

		TreeItem item = newTreeItem;
		while (item != null) {
			if (!item.getState()) item.setState(true);
			item = item.getParentItem();
		}

		newTreeItem.enterEditMode();
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {

		final Scope newItem = action.getScope();
		final ScopeTreeItem newTreeItem = getTreeItemFor(tree, newItem);

		if (newItem.isRoot()) throw new UnableToCompleteActionException("The action cannot be undone because an inconsistence was found.");
		final ScopeTreeItem childItem = newTreeItem.getChild(0);
		final TreeItem parentItem = newTreeItem.getParentItem();
		final int childIndex = parentItem.getChildIndex(newTreeItem);
		parentItem.removeItem(newTreeItem);

		parentItem.insertItem(childIndex, childItem);
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
