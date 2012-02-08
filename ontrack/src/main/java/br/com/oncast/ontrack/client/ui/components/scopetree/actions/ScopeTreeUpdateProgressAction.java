package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;
import br.com.oncast.ontrack.shared.model.action.KanbanAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ScopeTreeUpdateProgressAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final KanbanAction action;

	public ScopeTreeUpdateProgressAction(final ScopeTreeWidget tree, final KanbanAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context, final boolean isUserInteraction) throws ModelBeanNotFoundException {
		final Release release = context.findRelease(action.getReferenceId());
		for (final Scope scope : release.getScopeList()) {
			final ScopeTreeItem treeItem = tree.findScopeTreeItem(scope.getId());
			treeItem.setReferencedScope(context.findScope(scope.getId()));

			if (isUserInteraction) treeItem.getTree().setSelectedItem(treeItem);
		}

	}
}
