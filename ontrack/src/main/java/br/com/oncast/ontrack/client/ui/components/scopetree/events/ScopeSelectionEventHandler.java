package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.event.shared.EventHandler;

public interface ScopeSelectionEventHandler extends EventHandler {

	void onScopeSelectionRequest(Scope scope);

	boolean mustIgnoreFromSource(Object source);

}
