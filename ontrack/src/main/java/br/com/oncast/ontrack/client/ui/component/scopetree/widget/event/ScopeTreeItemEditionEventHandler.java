package br.com.oncast.ontrack.client.ui.component.scopetree.widget.event;

import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;

import com.google.gwt.event.shared.EventHandler;

public interface ScopeTreeItemEditionEventHandler extends EventHandler {
	void onItemUpdate(final ScopeTreeItem item);
}
