package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface ScopeInsertAction extends ScopeAction {
	UUID getNewScopeId();
}
