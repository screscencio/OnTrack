package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.NotFoundException;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.beans.Scope;

public class UpdateScopeTreeWidgetAction implements ScopeTreeWidgetAction {

	private final Scope scope;
	private final ScopeTreeItem treeItem;

	public UpdateScopeTreeWidgetAction(final ScopeTreeWidget tree, final ScopeAction action) throws UnableToCompleteActionException {
		this.scope = action.getScope();
		this.treeItem = getTreeItemFor(tree, scope);
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		treeItem.setDescription(scope.getDescription());
		treeItem.getTree().setSelectedItem(treeItem);
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		execute();
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
