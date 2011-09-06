package br.com.oncast.ontrack.client.ui.components;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface ComponentInteractionHandler {

	void onScopeSelectionRequest(final UUID scopeId);

}
