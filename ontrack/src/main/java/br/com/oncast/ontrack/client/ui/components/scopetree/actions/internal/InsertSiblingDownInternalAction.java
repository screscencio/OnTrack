package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertSiblingDownAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class InsertSiblingDownInternalAction implements InternalAction {

	private ScopeTreeItem newTreeItem;
	private final Scope scope;
	private ScopeTreeItem treeItem;

	public InsertSiblingDownInternalAction(final Scope scope) {
		this.scope = scope;
	}

	@Override
	public void execute(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		treeItem = InternalActionHelper.findScopeTreeItem(tree, scope);
		if (treeItem.isRoot()) throw new UnableToCompleteActionException("It is not possible to create a sibling for a root node.");
		newTreeItem = new ScopeTreeItem(new Scope(""));

		final ScopeTreeItem parentItem = treeItem.getParentItem();
		parentItem.insertItem(parentItem.getChildIndex(treeItem) + 1, newTreeItem);

		tree.setSelectedItem(newTreeItem);
		newTreeItem.enterEditMode();
	}

	@Override
	public void rollback(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		newTreeItem.remove();
		tree.setSelected(treeItem);
	}

	@Override
	public ModelAction createEquivalentModelAction(final String value) {
		return new ScopeInsertSiblingDownAction(scope.getId(), value);
	}
}
