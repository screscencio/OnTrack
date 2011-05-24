package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.shared.scope.actions.ScopeNotFoundException;

public interface ScopeTreeAction {
	void execute() throws ScopeNotFoundException;

	void rollback() throws ScopeNotFoundException;
}
