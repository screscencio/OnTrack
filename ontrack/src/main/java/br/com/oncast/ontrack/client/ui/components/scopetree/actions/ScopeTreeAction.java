package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.exceptions.ScopeNotFoundException;

public interface ScopeTreeAction {
	void execute(final ProjectContext context) throws ScopeNotFoundException;

	void rollback(final ProjectContext context) throws ScopeNotFoundException;
}
