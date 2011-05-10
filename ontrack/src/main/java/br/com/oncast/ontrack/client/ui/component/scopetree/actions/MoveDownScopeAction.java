package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.shared.beans.Scope;

public class MoveDownScopeAction implements ScopeAction {

	private final Scope selectedScope;

	public MoveDownScopeAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");

		final int index = selectedScope.getIndex();
		final Scope parent = selectedScope.getParent();
		if (isLastNode(index, parent)) return;

		parent.remove(selectedScope);
		parent.add(index + 1, selectedScope);
	}

	private boolean isLastNode(final int index, final Scope parent) {
		return parent.getChildren().size() - 1 == index;
	}
}