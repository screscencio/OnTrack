package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.NotFoundException;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.beans.Scope;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class MoveLeftScopeTreeWidgetAction implements ScopeTreeWidgetAction {

	private final ScopeTreeWidget tree;
	private final ScopeAction action;

	public MoveLeftScopeTreeWidgetAction(final ScopeTreeWidget tree, final ScopeAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		final ScopeTreeItem treeItem = getTreeItemFor(tree, action.getScope());
		if (treeItem.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");
		if (((ScopeTreeItem) treeItem.getParentItem()).isRoot()) throw new UnableToCompleteActionException(
				"It is not possible to move left when a node parent is a root node.");

		final Tree tree = treeItem.getTree();

		final TreeItem parentItem = treeItem.getParentItem();

		parentItem.getParentItem().insertItem(getNewPosition(parentItem), treeItem);

		tree.setSelectedItem(treeItem);
	}

	private int getNewPosition(final TreeItem parentItem) {
		return parentItem.getParentItem().getChildIndex(parentItem) + 1;
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		new MoveRightScopeTreeWidgetAction(tree, action).execute();
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
