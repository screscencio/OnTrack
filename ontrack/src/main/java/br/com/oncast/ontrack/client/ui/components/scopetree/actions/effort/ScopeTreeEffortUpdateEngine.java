package br.com.oncast.ontrack.client.ui.components.scopetree.actions.effort;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

// TODO +Optimize this; it is updating the entire tree; could it update only specific parts of it?
public class ScopeTreeEffortUpdateEngine {

	public static void process(final ScopeTreeWidget tree, final UUID referenceId) throws ScopeNotFoundException {
		processEntireTree(tree);
	}

	private static void processEntireTree(final ScopeTreeWidget tree) {
		final int count = tree.getItemCount();
		for (int i = 0; i < count; i++)
			processTopDown(tree.getItem(i));
	}

	private static void processTopDown(final ScopeTreeItem scopeTreeItem) {
		scopeTreeItem.getScopeTreeItemWidget().updateEffortDisplay();
		final int childCount = scopeTreeItem.getChildCount();
		for (int i = 0; i < childCount; i++)
			processTopDown(scopeTreeItem.getChild(i));
	}
}
