package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ScopeTreeTagAssociationAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ModelAction action;

	public ScopeTreeTagAssociationAction(final ScopeTreeWidget tree, final ModelAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context, ActionContext actionContext, final boolean isUserInteraction) throws ModelBeanNotFoundException {
		final Scope scope = context.findScope(action.getReferenceId());

		final ScopeTreeItem treeItem = tree.findScopeTreeItem(scope);
		treeItem.getScopeTreeItemWidget().updateTagsDisplay();
	}

}
