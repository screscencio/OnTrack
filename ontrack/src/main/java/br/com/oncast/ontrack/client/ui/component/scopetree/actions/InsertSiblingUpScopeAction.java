package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.beans.Scope;

public class InsertSiblingUpScopeAction implements ScopeAction {
	private final Scope selectedScope;

	public InsertSiblingUpScopeAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to create a sibling for a root node.");
		else selectedScope.getParent().add(selectedScope.getIndex(), new Scope("Novo scope Up"));
	}

	@Override
	public Scope getScope() {
		return selectedScope;
	}
}
