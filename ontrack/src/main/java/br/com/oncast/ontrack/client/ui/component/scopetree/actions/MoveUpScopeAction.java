package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.shared.beans.Scope;

public class MoveUpScopeAction implements ScopeAction {

	private final Scope selectedScope;

	public MoveUpScopeAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");

		final int index = selectedScope.getIndex();
		if (isFirstNode(index)) throw new UnableToCompleteActionException("It is not possible to move up the first node.");

		final Scope parent = selectedScope.getParent();
		parent.remove(selectedScope);
		parent.add(index - 1, selectedScope);
	}

	private boolean isFirstNode(final int index) {
		return index == 0;
	}
}
