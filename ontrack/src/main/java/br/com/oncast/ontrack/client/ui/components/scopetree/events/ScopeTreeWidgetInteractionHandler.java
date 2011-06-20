package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.client.ui.components.scopetree.actions.InternalActionRequestHandler;

import com.google.gwt.event.dom.client.KeyUpHandler;

public interface ScopeTreeWidgetInteractionHandler extends KeyUpHandler, ScopeTreeItemEditionEndEventHandler, ScopeTreeItemEditionCancelEventHandler,
		InternalActionRequestHandler {}
