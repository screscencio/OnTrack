package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

class ScopeTreeUpdateAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ModelAction action;
	private final boolean ignoreNotFoundScope;

	public ScopeTreeUpdateAction(final ScopeTreeWidget tree, final ModelAction action) {
		this(tree, action, false);
	}

	public ScopeTreeUpdateAction(final ScopeTreeWidget tree, final ModelAction action, final boolean ignoreNotFoundScope) {
		this.tree = tree;
		this.action = action;
		this.ignoreNotFoundScope = ignoreNotFoundScope;
	}

	@Override
	public void execute(final ProjectContext context, ActionContext actionContext, final boolean isUserInteraction) throws ScopeNotFoundException {
		// TODO ++Use treeItem.update(scope) instead of treeItem.setReferencedScope(scope).
		try {
			final Scope scope = context.findScope(action.getReferenceId());

			final ScopeTreeItem treeItem = tree.findScopeTreeItem(scope);
			treeItem.setReferencedScope(scope);
			treeItem.showDetailsIcon(ClientServiceProvider.getInstance().getAnnotationService().hasDetails(scope.getId()));

			if (isUserInteraction) treeItem.getTree().setSelectedItem(treeItem);
		}
		catch (final ScopeNotFoundException e) {
			if (!ignoreNotFoundScope) throw e;
		}

	}
}
