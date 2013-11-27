package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ReleaseRemoveAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ScopeTreeReleaseRemoveAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ReleaseRemoveAction action;

	public ScopeTreeReleaseRemoveAction(final ScopeTreeWidget tree, final ReleaseRemoveAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context, ActionContext actionContext, final boolean isUserInteraction) throws ScopeNotFoundException {
		for (final UUID scopeId : action.getDissociatedScopes()) {
			final Scope scope = context.findScope(scopeId);
			final ScopeTreeItem item = tree.findScopeTreeItem(scope);
			if (!item.isFake()) item.getScopeTreeItemWidget().updateReleaseDisplay();
		}
	}
}
