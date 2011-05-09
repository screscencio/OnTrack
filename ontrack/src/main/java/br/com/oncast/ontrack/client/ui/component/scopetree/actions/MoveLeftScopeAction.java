package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.shared.beans.Scope;

public class MoveLeftScopeAction implements ScopeAction {

	private final Scope selectedScope;

	public MoveLeftScopeAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
	}

	@Override
	public void execute() {
		if (selectedScope.isRoot()) return;
		if (selectedScope.getParent().isRoot()) return;

		final Scope parent = selectedScope.getParent();
		parent.remove(selectedScope);
		parent.getParent().add(parent.getIndex() + 1, selectedScope);
	}
}
