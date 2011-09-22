package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.event.shared.EventHandler;

public interface ScopeTreeItemDeclareProgressEventHandler extends EventHandler {

	void onDeclareProgressRequest(UUID scopeId, String progressDescription);
}
