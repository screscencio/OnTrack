package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class ScopeUpdateAction implements ScopeAction {

	private final Scope selectedScope;
	private final String description;
	private String oldDescription;

	public ScopeUpdateAction(final Scope scope, final String description) {
		this.selectedScope = scope;
		this.description = description;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		oldDescription = selectedScope.getDescription();
		selectedScope.setDescription(description);
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		if (oldDescription == null) throw new UnableToCompleteActionException("The action cannot be rolled back because it has never been executed.");
		selectedScope.setDescription(oldDescription);
	}

	@Override
	public Scope getScope() {
		return selectedScope;
	}
}
