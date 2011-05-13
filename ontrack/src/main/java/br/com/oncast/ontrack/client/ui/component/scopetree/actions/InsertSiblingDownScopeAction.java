package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.beans.Scope;

public class InsertSiblingDownScopeAction implements InsertionScopeAction {
	private final Scope selectedScope;
	private final Scope newScope;

	public InsertSiblingDownScopeAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
		newScope = new Scope("");
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to create a sibling for a root node.");
		selectedScope.getParent().add(selectedScope.getIndex() + 1, newScope);
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
