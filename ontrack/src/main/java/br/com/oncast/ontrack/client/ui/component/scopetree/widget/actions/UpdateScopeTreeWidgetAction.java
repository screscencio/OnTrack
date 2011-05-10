package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.shared.beans.Scope;

public class UpdateScopeTreeWidgetAction implements ScopeTreeWidgetAction {

	private final Scope scope;
	private final ScopeTreeItem treeItem;

	public UpdateScopeTreeWidgetAction(final ScopeTreeItem selected, final Scope scope) {
		this.treeItem = selected;
		this.scope = scope;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		treeItem.setDescription(scope.getDescription());
		treeItem.getTree().setSelectedItem(treeItem);
	}
}
