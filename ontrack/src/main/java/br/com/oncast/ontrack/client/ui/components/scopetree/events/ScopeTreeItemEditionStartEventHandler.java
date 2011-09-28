package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;

import com.google.gwt.event.shared.EventHandler;

public interface ScopeTreeItemEditionStartEventHandler extends EventHandler {
	void onItemEditionStart(final ScopeTreeItem item);
}
