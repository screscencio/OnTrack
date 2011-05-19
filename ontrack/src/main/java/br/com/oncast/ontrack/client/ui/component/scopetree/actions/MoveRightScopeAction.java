package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.beans.Scope;

public class MoveRightScopeAction implements ScopeAction {

	private final Scope selectedScope;

	public MoveRightScopeAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");

		final int index = selectedScope.getIndex();
		if (isFirstNode(index)) throw new UnableToCompleteActionException(
				"The action cannot be processed because there is no node where this node could be moded into.");

		final Scope upperSibling = getUpperSibling();
		selectedScope.getParent().remove(selectedScope);
		upperSibling.add(selectedScope);
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		new MoveLeftScopeAction(selectedScope).execute();
	}

	private Scope getUpperSibling() {
		return selectedScope.getParent().getChildren().get(selectedScope.getIndex() - 1);
	}

	private boolean isFirstNode(final int index) {
		return index == 0;
	}

	@Override
	public Scope getScope() {
		return selectedScope;
	}
}