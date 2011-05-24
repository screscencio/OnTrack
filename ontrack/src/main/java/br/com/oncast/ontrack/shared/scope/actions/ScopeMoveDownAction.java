package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class ScopeMoveDownAction implements ScopeMoveAction {

	private final Scope selectedScope;

	public ScopeMoveDownAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");

		final Scope parent = selectedScope.getParent();
		final int index = parent.getChildIndex(selectedScope);

		if (isLastNode(index, parent)) throw new UnableToCompleteActionException("It is not possible to move down the node when it is the last node.");

		parent.remove(selectedScope);
		parent.add(index + 1, selectedScope);
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		new ScopeMoveUpAction(selectedScope).execute();
	}

	private boolean isLastNode(final int index, final Scope parent) {
		return parent.getChildren().size() - 1 == index;
	}

	@Override
	public Scope getScope() {
		return selectedScope;
	}
}