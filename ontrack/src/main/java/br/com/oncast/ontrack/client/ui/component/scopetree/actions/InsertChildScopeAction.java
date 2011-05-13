package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.beans.Scope;

public class InsertChildScopeAction implements InsertionScopeAction {

	private final Scope selectedScope;
	private final Scope newScope;

	public InsertChildScopeAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
		newScope = new Scope("");
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		selectedScope.add(newScope);
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		new RemoveScopeAction(newScope).execute();
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
