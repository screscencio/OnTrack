package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class DeclareProgressInternalAction implements InternalAction {

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
		if (selectedTreeItem.getChildCount() > 0) throw new UnableToCompleteActionException("Progress can only be assigned to leaf scope items.");

		tree.setSelected(null);
		selectedTreeItem.getScopeTreeItemWidget().showProgressMenu(context.getProgressDefinitions());
	}

	@Override
	public void rollback(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		tree.setSelected(selectedTreeItem);
	}

	@Override
	public ModelAction createEquivalentModelAction(final String value) {
		return new ScopeDeclareProgressAction(scope.getId(), value);
	}

}
