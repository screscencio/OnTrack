package br.com.oncast.ontrack.client.ui.components.scopetree.actions.effort;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

// TODO Update it so that it refreshes only till finds the ones that have not changed.
public class ScopeTreeEffortUpdateEngine {

	public static void process(final ScopeTreeWidget tree, final UUID referenceId) throws ScopeNotFoundException {
		// FIXME
		// final ScopeTreeItem scopeTreeItem = tree.findScopeTreeItem(referenceId);
		// processBottomUp(scopeTreeItem);
		// processTopDown(scopeTreeItem);

		ScopeTreeItem scopeTreeItem = tree.findScopeTreeItem(referenceId);
		while (!scopeTreeItem.isRoot())
			scopeTreeItem = scopeTreeItem.getParentItem();
		processTopDown(scopeTreeItem);
	}

	// FIXME
	// private static void processBottomUp(final ScopeTreeItem scopeTreeItem) {
	// final ScopeTreeItemWidget scopeWidget = scopeTreeItem.getScopeTreeItemWidget();
	// // if (scopeWidget == null) return;
	// scopeWidget.refreshEffort();
	//
	// if (scopeTreeItem.isRoot()) return;
	// processBottomUp(scopeTreeItem.getParentItem());
	// }

	private static void processTopDown(final ScopeTreeItem scopeTreeItem) {
		final int childCount = scopeTreeItem.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final ScopeTreeItem child = scopeTreeItem.getChild(i);
			child.getScopeTreeItemWidget().refreshEffort();
			processTopDown(child);
		}
	}
}
