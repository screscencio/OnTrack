package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

public class ScopeTreeReleaseAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;

	public ScopeTreeReleaseAction(final ScopeTreeWidget tree) {
		this.tree = tree;
	}

	@Override
	public void execute(final ProjectContext context, final boolean isUserInteraction) throws ScopeNotFoundException {
		final int count = tree.getItemCount();
		for (int i = 0; i < count; i++)
			updateItemHierarchy(tree.getItem(i));
	}

	private void updateItemHierarchy(final ScopeTreeItem scopeTreeItem) {
		scopeTreeItem.getScopeTreeItemWidget().updateReleaseDisplay();
		final int childCount = scopeTreeItem.getChildCount();
		for (int i = 0; i < childCount; i++)
			updateItemHierarchy(scopeTreeItem.getChild(i));
	}
}
