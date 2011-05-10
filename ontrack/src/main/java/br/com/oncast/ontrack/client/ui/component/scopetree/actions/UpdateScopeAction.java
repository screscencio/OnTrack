package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.beans.Scope;

public class UpdateScopeAction implements ScopeAction {

	private final Scope selectedScope;
	private final String description;

	public UpdateScopeAction(final Scope scope, final String description) {
		this.selectedScope = scope;
		this.description = description;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		selectedScope.setDescription(description);
	}

	@Override
	public Scope getScope() {
		return selectedScope;
	}
}
