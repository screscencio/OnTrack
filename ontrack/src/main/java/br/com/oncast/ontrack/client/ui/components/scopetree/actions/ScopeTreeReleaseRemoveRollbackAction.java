package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ReleaseRemoveRollbackAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

public class ScopeTreeReleaseRemoveRollbackAction implements ScopeTreeAction {

	private final ScopeTreeWidget tree;
	private final ReleaseRemoveRollbackAction action;

	public ScopeTreeReleaseRemoveRollbackAction(final ScopeTreeWidget tree, final ReleaseRemoveRollbackAction action) {
		this.tree = tree;
		this.action = action;
	}

	@Override
	public void execute(final ProjectContext context, ActionContext actionContext, final boolean isUserInteraction) throws ScopeNotFoundException {
		try {
			final Release release = context.findRelease(action.getNewReleaseId());
			for (final Scope scope : release.getScopeList()) {
				final ScopeTreeItem item = tree.findScopeTreeItem(scope);
				if (!item.isFake()) item.getScopeTreeItemWidget().updateReleaseDisplay();
			}
		} catch (final ReleaseNotFoundException e) {}

	}
}
