package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.shared.beans.Scope;

public class MoveUpScopeAction implements ScopeAction {

	private final Scope selectedScope;

	public MoveUpScopeAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
	}

	@Override
	public void execute() {
		if (selectedScope.isRoot()) return;

		final int index = selectedScope.getIndex();
		if (index == 0) return;

		final Scope parent = selectedScope.getParent();
		parent.remove(selectedScope);
		parent.add(index - 1, selectedScope);
	}
}
