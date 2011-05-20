package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.beans.Scope;

public class InsertFatherScopeAction implements ScopeAction {
	private Scope selectedScope;
	private final Scope newScope;

	public InsertFatherScopeAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
		newScope = new Scope("");
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to create a father for a root node.");

		final Scope parent = selectedScope.getParent();
		final int index = selectedScope.getIndex();
		parent.remove(selectedScope);
		parent.add(index, newScope);
		newScope.add(selectedScope);
		selectedScope = newScope;
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		if (newScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to remove a root node.");
		final Scope child = newScope.getChildren().get(0);
		final Scope parent = newScope.getParent();
		final int index = newScope.getIndex();
		parent.remove(newScope);

		parent.add(index, child);
	}

	@Override
	public Scope getScope() {
		return selectedScope;
	}
}