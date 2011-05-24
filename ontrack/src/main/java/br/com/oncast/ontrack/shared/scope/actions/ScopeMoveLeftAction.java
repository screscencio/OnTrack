package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class ScopeMoveLeftAction implements ScopeMoveAction {

	private final Scope selectedScope;
	private int oldIndex;

	public ScopeMoveLeftAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");
		if (selectedScope.getParent().isRoot()) throw new UnableToCompleteActionException("It is not possible to move left when a node parent is a root node.");

		final Scope parent = selectedScope.getParent();
		final Scope grandParent = parent.getParent();
		oldIndex = parent.getChildIndex(selectedScope);

		parent.remove(selectedScope);
		grandParent.add(grandParent.getChildIndex(parent) + 1, selectedScope);
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");

		final Scope parent = selectedScope.getParent();
		final int index = parent.getChildIndex(selectedScope);

		if (isFirstNode(index)) throw new UnableToCompleteActionException(
				"The action cannot be processed because there is no node where this node could be moded into.");

		final Scope upperSibling = parent.getChildren().get(index - 1);
		selectedScope.getParent().remove(selectedScope);
		upperSibling.add(oldIndex, selectedScope);
	}

	@Override
	public Scope getScope() {
		return selectedScope;
	}

	private boolean isFirstNode(final int index) {
		return index == 0;
	}
}
