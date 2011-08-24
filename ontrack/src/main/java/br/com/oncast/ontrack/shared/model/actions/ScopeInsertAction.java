package br.com.oncast.ontrack.shared.model.actions;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface ScopeInsertAction extends ScopeAction {
	UUID getNewScopeId();
}
