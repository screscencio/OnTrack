package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.NotFoundException;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.beans.Scope;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class RemoveScopeTreeWidgetAction implements ScopeTreeWidgetAction {

	private final Scope referencedScope;
	private final ScopeTreeWidget tree;

	public RemoveScopeTreeWidgetAction(final ScopeTreeWidget tree, final ScopeAction action) {
		this.tree = tree;
		this.referencedScope = action.getScope();
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		final ScopeTreeItem treeItem = getTreeItemFor(referencedScope);
		if (treeItem.isRoot()) throw new UnableToCompleteActionException("It is not possible to remove a root node.");

		final Tree tree = treeItem.getTree();

		final TreeItem parentItem = treeItem.getParentItem();
		final int childIndex = parentItem.getChildIndex(treeItem);
		parentItem.removeItem(treeItem);

		tree.setSelectedItem(((parentItem.getChildCount() > 0) ? parentItem.getChild((parentItem.getChildCount() - 1 < childIndex) ? parentItem.getChildCount() - 1
				: childIndex)
				: parentItem));
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		final Scope parentScope = referencedScope.getParent();
		final int childIndex = referencedScope.getIndex();
		final ScopeTreeItem parentItem = getTreeItemFor(parentScope);
		parentItem.insertItem(childIndex, new ScopeTreeItem(referencedScope));
	}

	private ScopeTreeItem getTreeItemFor(final Scope scope) throws UnableToCompleteActionException {
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
