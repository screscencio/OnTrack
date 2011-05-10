package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.shared.beans.Scope;

public class MoveLeftScopeAction implements ScopeAction {

	private final Scope selectedScope;

	public MoveLeftScopeAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");
		if (selectedScope.getParent().isRoot()) throw new UnableToCompleteActionException("It is not possible to move left when a node parent is a root node.");

		final Scope parent = selectedScope.getParent();
		parent.remove(selectedScope);
		parent.getParent().add(parent.getIndex() + 1, selectedScope);
	}
}
