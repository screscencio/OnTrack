package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.event.shared.EventHandler;

public interface ScopeTreeItemEditionEndEventHandler extends EventHandler {
	void onItemEditionEnd(final Scope scope, final String value);
}
