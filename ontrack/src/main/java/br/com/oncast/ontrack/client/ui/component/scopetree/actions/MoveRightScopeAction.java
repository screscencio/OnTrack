package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.shared.beans.Scope;

public class MoveRightScopeAction implements ScopeAction {

	private final Scope selectedScope;

	public MoveRightScopeAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
	}

	@Override
	public void execute() {
		if (selectedScope.isRoot()) return;
		if (selectedScope.getIndex() == 0) return;

		final Scope sibling = selectedScope.getParent().getChildren().get(selectedScope.getIndex() - 1);
		selectedScope.getParent().remove(selectedScope);
		sibling.add(selectedScope);
	}
}