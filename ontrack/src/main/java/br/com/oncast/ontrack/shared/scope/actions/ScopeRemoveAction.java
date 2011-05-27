package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class ScopeRemoveAction implements ScopeAction {

	private final Scope selectedScope;
	private Scope parent;
	private int index;

	public ScopeRemoveAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
	}

	@Override
	public void execute(final ProjectContext context) throws UnableToCompleteActionException {
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to remove a root node.");
		parent = selectedScope.getParent();
		index = parent.getChildIndex(selectedScope);
		parent.remove(selectedScope);
	}

	@Override
	public void rollback(final ProjectContext context) throws UnableToCompleteActionException {
		parent.add(index, selectedScope);
	}

	@Override
	public Scope getScope() {
		return selectedScope;
	}
}