package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.NotFoundException;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.beans.Scope;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class MoveDownScopeTreeWidgetAction implements ScopeTreeWidgetAction {

	private final ScopeTreeWidget tree;
	private final ScopeAction action;

	public MoveDownScopeTreeWidgetAction(final ScopeTreeWidget tree, final ScopeAction action) throws UnableToCompleteActionException {
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
		if (isLastNode(index, parentItem)) throw new UnableToCompleteActionException("It is not possible to move down the last node.");

		parentItem.insertItem(index + 1, treeItem);

		tree.setSelectedItem(treeItem);
	}

	private boolean isLastNode(final int index, final TreeItem parentItem) {
		return parentItem.getChildCount() - 1 == index;
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		new MoveUpScopeTreeWidgetAction(tree, action).execute();
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
