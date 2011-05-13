package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class MoveDownScopeTreeWidgetAction implements ScopeTreeWidgetAction {

	private final ScopeTreeItem treeItem;

	public MoveDownScopeTreeWidgetAction(final ScopeTreeItem treeItem) {
		this.treeItem = treeItem;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
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
		new MoveUpScopeTreeWidgetAction(treeItem).execute();
	}
}
