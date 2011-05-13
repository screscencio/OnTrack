package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.beans.Scope;

public class RemoveScopeAction implements ScopeAction {
	private final Scope selectedScope;
	private Scope parent;
	private int index;

	public RemoveScopeAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to remove a root node.");
		parent = selectedScope.getParent();
		index = selectedScope.getIndex();
		parent.remove(selectedScope);
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		if (parent == null) throw new UnableToCompleteActionException("The action cannot be rolled back because it has never being executed.");
		parent.add(index, selectedScope);
	}

	@Override
	public Scope getScope() {
		return selectedScope;
	}
}