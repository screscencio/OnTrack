package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class ScopeMoveUpAction implements ScopeMoveAction {

	private final Scope selectedScope;

	public ScopeMoveUpAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");

		final Scope parent = selectedScope.getParent();
		final int index = parent.getChildIndex(selectedScope);

		if (isFirstNode(index)) throw new UnableToCompleteActionException("It is not possible to move up the first node.");

		parent.remove(selectedScope);
		parent.add(index - 1, selectedScope);
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		new ScopeMoveDownAction(selectedScope).execute();
	}

	private boolean isFirstNode(final int index) {
		return index == 0;
	}

	@Override
	public Scope getScope() {
		return selectedScope;
	}
}