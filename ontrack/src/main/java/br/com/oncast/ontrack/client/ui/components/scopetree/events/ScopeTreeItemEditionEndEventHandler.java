package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;

import com.google.gwt.event.shared.EventHandler;

public interface ScopeTreeItemEditionEndEventHandler extends EventHandler {
	void onItemEditionEnd(final ScopeTreeItem item, final String value);
}
