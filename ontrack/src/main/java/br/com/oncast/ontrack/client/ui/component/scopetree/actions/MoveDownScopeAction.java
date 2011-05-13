package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
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
		if (isLastNode(index, parent)) throw new UnableToCompleteActionException("It is not possible to move down the node when it is the last node.");

		parent.remove(selectedScope);
		parent.add(index + 1, selectedScope);
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		new MoveUpScopeAction(selectedScope).execute();
	}

	private boolean isLastNode(final int index, final Scope parent) {
		return parent.getChildren().size() - 1 == index;
	}

	@Override
	public Scope getScope() {
		return selectedScope;
	}
}