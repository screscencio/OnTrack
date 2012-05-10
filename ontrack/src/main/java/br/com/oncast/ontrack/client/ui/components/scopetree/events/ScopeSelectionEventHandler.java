package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import com.google.gwt.event.shared.EventHandler;

public interface ScopeSelectionEventHandler extends EventHandler {

	void onScopeSelectionRequest(ScopeSelectionEvent event);

}
