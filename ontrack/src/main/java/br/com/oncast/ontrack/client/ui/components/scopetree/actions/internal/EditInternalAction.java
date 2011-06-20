package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class EditInternalAction implements InternalAction {

	private final Scope scope;

	public EditInternalAction(final Scope scope) {
		this.scope = scope;
	}

	@Override
	public void execute(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		tree.getSelected().enterEditMode();
	}

	@Override
	public void rollback(final ScopeTreeWidget tree) throws UnableToCompleteActionException {}

	@Override
	public ModelAction createEquivalentModelAction(final String newPattern) {
		return new ScopeUpdateAction(scope, newPattern);
	}

}
