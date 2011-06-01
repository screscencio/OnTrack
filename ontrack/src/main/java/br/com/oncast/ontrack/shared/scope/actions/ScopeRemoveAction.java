package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class ScopeRemoveAction implements ScopeAction {

	private final Scope selectedScope;
	private Scope parent;
	private int index;
	private Release release;

	public ScopeRemoveAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
	}

	@Override
	public void execute(final ProjectContext context) throws UnableToCompleteActionException {
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to remove a root node.");
		parent = selectedScope.getParent();
		index = parent.getChildIndex(selectedScope);
		parent.remove(selectedScope);
		release = selectedScope.getRelease();
		if (release != null) release.removeScope(selectedScope);
		selectedScope.setRelease(null);
	}

	@Override
	public void rollback(final ProjectContext context) throws UnableToCompleteActionException {
		parent.add(index, selectedScope);
		selectedScope.setRelease(release);
		if (release != null) release.addScope(selectedScope);
	}

	@Override
	public Scope getScope() {
		return selectedScope;
	}
}