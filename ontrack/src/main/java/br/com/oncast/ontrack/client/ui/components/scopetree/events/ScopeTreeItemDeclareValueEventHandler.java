package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.event.shared.EventHandler;

public interface ScopeTreeItemDeclareValueEventHandler extends EventHandler {

	void onDeclareValueRequest(UUID scopeId, String valueDescription);
}
