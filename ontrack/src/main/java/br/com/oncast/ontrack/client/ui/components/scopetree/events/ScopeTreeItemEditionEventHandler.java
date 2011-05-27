package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;

import com.google.gwt.event.shared.EventHandler;

public interface ScopeTreeItemEditionEventHandler extends EventHandler {
	void onItemUpdate(final ScopeTreeItem item, final String newContent);
}
