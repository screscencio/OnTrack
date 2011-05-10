package br.com.oncast.ontrack.client.ui.component.scopetree.widget;

import com.google.gwt.event.dom.client.KeyUpHandler;

public interface ScopeTreeWidgetInteractionHandler extends KeyUpHandler {
	void onItemUpdate(final ScopeTreeItem item);
}
