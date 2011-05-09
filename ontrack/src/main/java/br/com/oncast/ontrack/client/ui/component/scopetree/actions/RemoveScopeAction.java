package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.shared.beans.Scope;

public class RemoveScopeAction implements ScopeAction {
	private final Scope selectedScope;

	public RemoveScopeAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
	}

	@Override
	public void execute() {
		if (selectedScope.isRoot()) return;

		selectedScope.getParent().remove(selectedScope);

		return;
	}

}
