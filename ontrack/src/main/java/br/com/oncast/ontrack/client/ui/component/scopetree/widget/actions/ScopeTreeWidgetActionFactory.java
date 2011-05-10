package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.shared.beans.Scope;

public interface ScopeTreeWidgetActionFactory {
	ScopeTreeWidgetAction create(ScopeTreeItem referencedScopeTreeItem, Scope referencedScope);
}
