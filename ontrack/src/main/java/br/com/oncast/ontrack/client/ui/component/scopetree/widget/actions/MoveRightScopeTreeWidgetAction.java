package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class MoveRightScopeTreeWidgetAction implements ScopeTreeWidgetAction {

	private final ScopeTreeItem treeItem;

	public MoveRightScopeTreeWidgetAction(final ScopeTreeItem treeItem) {
		this.treeItem = treeItem;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		if (treeItem.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");

		final Tree tree = treeItem.getTree();

		final TreeItem parentItem = treeItem.getParentItem();
		final int index = parentItem.getChildIndex(treeItem);

		if (isFirstNode(index)) throw new UnableToCompleteActionException(
				"It is not possible to move right the first node, because it will be moved to a node above it.");

		final TreeItem upperSibling = getUpperSibling(parentItem, index);
		treeItem.remove();
		upperSibling.addItem(treeItem);

		tree.setSelectedItem(treeItem);
		tree.getSelectedItem().getParentItem().setState(true);
	}

	private TreeItem getUpperSibling(final TreeItem parentItem, final int index) {
		return parentItem.getChild(index - 1);
	}

	private boolean isFirstNode(final int index) {
		return index == 0;
	}
}
