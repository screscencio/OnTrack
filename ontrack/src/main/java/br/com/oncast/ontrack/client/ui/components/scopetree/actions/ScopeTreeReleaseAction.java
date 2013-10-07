package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRemoveAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.ArrayList;
import java.util.List;

public class ScopeTreeReleaseAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ModelAction action;

	public ScopeTreeReleaseAction(final ScopeTreeWidget tree, final ModelAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context, final ActionContext actionContext, final boolean isUserInteraction) throws ScopeNotFoundException {
		List<Scope> scopesList;
		try {
			final Release release = context.findRelease(action.getReferenceId());
			scopesList = release.getScopeList();
		} catch (final ReleaseNotFoundException e) {
			scopesList = new ArrayList<Scope>();
			if (action instanceof ReleaseRemoveAction) {
				for (final UUID scopeId : ((ReleaseRemoveAction) action).getDissociatedScopes()) {
					scopesList.add(context.findScope(scopeId));
				}
			}
		}

		for (final Scope scope : scopesList) {
			final ScopeTreeItem item = tree.findScopeTreeItem(scope);
			if (!item.isFake()) item.getScopeTreeItemWidget().updateReleaseDisplay();
		}
	}
}
