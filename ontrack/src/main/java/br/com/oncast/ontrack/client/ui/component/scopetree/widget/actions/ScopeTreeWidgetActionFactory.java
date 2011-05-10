package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;

public interface ScopeTreeWidgetActionFactory {
	ScopeTreeWidgetAction create(ScopeTreeWidget tree, ScopeAction action);
}
