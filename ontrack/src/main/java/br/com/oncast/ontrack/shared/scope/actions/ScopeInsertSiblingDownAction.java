package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class ScopeInsertSiblingDownAction implements ScopeInsertSiblingAction {
	private final Scope selectedScope;
	private final Scope newScope;

	public ScopeInsertSiblingDownAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
		newScope = new Scope("");
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to create a sibling for a root node.");

		final Scope parent = selectedScope.getParent();
		parent.add(parent.getChildIndex(selectedScope) + 1, newScope);
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		new ScopeRemoveAction(newScope).execute();
	}

	@Override
	public Scope getScope() {
		return selectedScope;
	}

	@Override
	public Scope getNewScope() {
		return newScope;
	}
}
