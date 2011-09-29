package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.event.shared.EventHandler;

public interface ScopeTreeItemDeclareEffortEventHandler extends EventHandler {

	void onDeclareEffortRequest(UUID scopeId, String effortDescription);
}
