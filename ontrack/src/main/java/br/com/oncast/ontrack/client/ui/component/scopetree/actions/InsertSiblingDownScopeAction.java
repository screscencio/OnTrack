package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.shared.beans.Scope;

public class InsertSiblingDownScopeAction implements ScopeAction {
	private final Scope selectedScope;

	public InsertSiblingDownScopeAction(final Scope selectedScope) {
		this.selectedScope = selectedScope;
	}

	@Override
	public void execute() {
		if (selectedScope.isRoot()) selectedScope.add(new Scope("Novo scopo Down"));
		else selectedScope.getParent().add(selectedScope.getIndex() + 1, new Scope("Novo scope Down"));

		return;
	}

}
