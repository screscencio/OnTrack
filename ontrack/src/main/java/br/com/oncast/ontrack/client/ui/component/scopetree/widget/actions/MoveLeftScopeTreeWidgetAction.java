package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class MoveLeftScopeTreeWidgetAction implements ScopeTreeWidgetAction {

	private final ScopeTreeItem treeItem;

	public MoveLeftScopeTreeWidgetAction(final ScopeTreeItem treeItem) {
		this.treeItem = treeItem;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		if (treeItem.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");
		if (((ScopeTreeItem) treeItem.getParentItem()).isRoot()) throw new UnableToCompleteActionException(
				"It is not possible to move left when a node parent is a root node.");

		final Tree tree = treeItem.getTree();

		final TreeItem parentItem = treeItem.getParentItem();

		treeItem.remove();
		parentItem.getParentItem().insertItem(getNewPosition(parentItem), treeItem);

		tree.setSelectedItem(treeItem);
	}

	private int getNewPosition(final TreeItem parentItem) {
		return parentItem.getParentItem().getChildIndex(parentItem) + 1;
	}
}
