package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.shared.beans.Scope;

public class InsertSiblingUpScopeAction implements ScopeAction {
	private final Scope selectedScope;

	public InsertSiblingUpScopeAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
	}

	@Override
	public void execute() {
		if (selectedScope.isRoot()) return;
		else selectedScope.getParent().add(selectedScope.getIndex(), new Scope("Novo scope Up"));

		return;
	}
}
