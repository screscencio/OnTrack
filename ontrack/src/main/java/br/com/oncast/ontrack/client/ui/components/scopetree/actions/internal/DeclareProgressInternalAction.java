package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.exceptions.OperationNotAllowedException;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class DeclareProgressInternalAction implements OneStepInternalAction {

	private ScopeTreeItem selectedTreeItem;
	private final Scope scope;
	private final ProjectContext context;

	public DeclareProgressInternalAction(final ProjectContext context, final Scope scope) {
		this.context = context;
		this.scope = scope;
	}

	@Override
	public void execute(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		selectedTreeItem = InternalActionHelper.findScopeTreeItem(tree, scope);
		if (selectedTreeItem.getChildCount() > 0) throw new OperationNotAllowedException("Progress can only be assigned to leaf scope items.");

		tree.setSelected(null);
		selectedTreeItem.getScopeTreeItemWidget().showProgressMenu(context.getProgressDefinitions());
	}
}
