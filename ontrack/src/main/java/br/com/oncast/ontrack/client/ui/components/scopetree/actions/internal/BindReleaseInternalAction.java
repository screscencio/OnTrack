package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class BindReleaseInternalAction implements InternalAction {

	private final ProjectContext context;
	private final Scope scope;
	private ScopeTreeItem selectedTreeItem;

	public BindReleaseInternalAction(final ProjectContext context, final Scope scope) {
		this.context = context;
		this.scope = scope;
	}

	@Override
	public void execute(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		selectedTreeItem = InternalActionHelper.findScopeTreeItem(tree, scope);
		tree.setSelected(null);
		selectedTreeItem.getScopeTreeItemWidget().showBindReleaseMenu(context.getReleaseHierarchy());
	}

	@Override
	public void rollback(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		tree.setSelected(selectedTreeItem);
	}

	@Override
	public ModelAction createEquivalentModelAction(final String value) {
		return new ScopeBindReleaseAction(scope.getId(), value);
	}

}
