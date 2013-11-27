package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.TagRemoveAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ScopeTreeTagRemoveUpdateAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final TagRemoveAction action;

	public ScopeTreeTagRemoveUpdateAction(final ScopeTreeWidget tree, final TagRemoveAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context, ActionContext actionContext, final boolean isUserInteraction) throws ModelBeanNotFoundException {
		for (final UUID scopeId : action.getRemovedScopes()) {
			final Scope scope = context.findScope(scopeId);
			tree.findScopeTreeItem(scope).getScopeTreeItemWidget().updateTagsDisplay();
		}
	}

}
