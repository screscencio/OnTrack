package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.client.utils.jquery.EventHandler;

public interface ScopeTreeWidgetInteractionHandler extends EventHandler,
		ScopeTreeItemEditionStartEventHandler,
		ScopeTreeItemEditionEndEventHandler,
		ScopeTreeItemEditionCancelEventHandler,
		ScopeTreeItemBindReleaseEventHandler,
		ScopeTreeItemDeclareProgressEventHandler,
		ScopeTreeItemDeclareEffortEventHandler,
		ScopeTreeItemDeclareValueEventHandler {}
