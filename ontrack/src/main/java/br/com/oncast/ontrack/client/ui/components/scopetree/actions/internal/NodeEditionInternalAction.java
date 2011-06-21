package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class NodeEditionInternalAction implements InternalAction {

	private final Scope scope;
	private ScopeTreeItem selectedTreeItem;

	public NodeEditionInternalAction(final Scope scope) {
		this.scope = scope;
	}

	@Override
	public void execute(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		selectedTreeItem = tree.getSelected();
		selectedTreeItem.enterEditMode();
	}

	@Override
	public void rollback(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		tree.setSelected(selectedTreeItem);
	}

	@Override
	public ModelAction createEquivalentModelAction(final String newPattern) {
		return new ScopeUpdateAction(scope, newPattern);
	}

}
