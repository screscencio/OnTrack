package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.NotFoundException;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.beans.Scope;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class MoveRightScopeTreeWidgetAction implements ScopeTreeWidgetAction {

	private final ScopeTreeWidget tree;
	private final ScopeAction action;

	public MoveRightScopeTreeWidgetAction(final ScopeTreeWidget tree, final ScopeAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		final ScopeTreeItem treeItem = getTreeItemFor(tree, action.getScope());
		if (treeItem.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");

		final Tree tree = treeItem.getTree();

		final TreeItem parentItem = treeItem.getParentItem();
		final int index = parentItem.getChildIndex(treeItem);

		if (isFirstNode(index)) throw new UnableToCompleteActionException(
				"It is not possible to move right the first node, because it will be moved to a node above it.");

		final TreeItem upperSibling = getUpperSibling(parentItem, index);
		upperSibling.addItem(treeItem);

		tree.setSelectedItem(treeItem);
		tree.getSelectedItem().getParentItem().setState(true);
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		new MoveLeftScopeTreeWidgetAction(tree, action).execute();
	}

	private TreeItem getUpperSibling(final TreeItem parentItem, final int index) {
		return parentItem.getChild(index - 1);
	}

	private boolean isFirstNode(final int index) {
		return index == 0;
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
