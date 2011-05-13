package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class RemoveScopeTreeWidgetAction implements ScopeTreeWidgetAction {

	private final ScopeTreeItem treeItem;
	private TreeItem parentItem;
	private int childIndex;

	public RemoveScopeTreeWidgetAction(final ScopeTreeItem treeItem) {
		this.treeItem = treeItem;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		if (treeItem.isRoot()) throw new UnableToCompleteActionException("It is not possible to remove a root node.");

		final Tree tree = treeItem.getTree();

		parentItem = treeItem.getParentItem();
		childIndex = parentItem.getChildIndex(treeItem);
		parentItem.removeItem(treeItem);

		tree.setSelectedItem(((parentItem.getChildCount() > 0) ? parentItem.getChild((parentItem.getChildCount() - 1 < childIndex) ? parentItem.getChildCount() - 1
				: childIndex)
				: parentItem));
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		if (parentItem == null) throw new UnableToCompleteActionException("The action cannot be rolled back because it has never being executed.");
		parentItem.insertItem(childIndex, treeItem);
	}
}
