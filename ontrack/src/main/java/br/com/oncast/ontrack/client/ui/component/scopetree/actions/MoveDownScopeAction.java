package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.shared.beans.Scope;

public class MoveDownScopeAction implements ScopeAction {

	private final Scope selectedScope;

	public MoveDownScopeAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
	}

	@Override
	public void execute() {
		if (selectedScope.isRoot()) return;

		final int index = selectedScope.getIndex();
		final Scope parent = selectedScope.getParent();
		if (!(parent.getChildren().size() - 1 > index)) return;

		parent.remove(selectedScope);
		parent.add(index + 1, selectedScope);
	}
}