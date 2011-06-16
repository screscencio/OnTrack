package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.util.uuid.UUID;

public interface ScopeInsertAction extends ScopeAction {
	UUID getNewScopeId();
}
