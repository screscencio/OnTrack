package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class ScopeInsertChildAction implements ScopeInsertAction {

	private final Scope selectedScope;
	private final Scope newScope;

	public ScopeInsertChildAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
		newScope = new Scope("");
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		selectedScope.add(newScope);
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		new ScopeRemoveAction(newScope).execute();
	}

	@Override
	public Scope getScope() {
		return selectedScope;
	}

	@Override
	public Scope getNewScope() {
		return newScope;
	}
}
