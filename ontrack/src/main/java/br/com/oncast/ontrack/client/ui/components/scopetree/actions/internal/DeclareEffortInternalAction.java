package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareEffortAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class DeclareEffortInternalAction implements InternalAction {

	private ScopeTreeItem selectedTreeItem;
	private final Scope scope;
	private final ProjectContext projectContext;

	public DeclareEffortInternalAction(final Scope scope, final ProjectContext projectContext) {
		this.scope = scope;
		this.projectContext = projectContext;
	}

	@Override
	public void execute(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		selectedTreeItem = InternalActionHelper.findScopeTreeItem(tree, scope);
		tree.setSelected(null);
		selectedTreeItem.getScopeTreeItemWidget().showEffortMenu(projectContext.getFibonacciScaleForEffort());
	}

	@Override
	public void rollback(final ScopeTreeWidget tree) throws UnableToCompleteActionException {}

	@Override
	public ModelAction createEquivalentModelAction(final String value) {
		float declaredEffort;
		boolean hasDeclaredEffort;

		try {
			declaredEffort = Float.valueOf(value);
			hasDeclaredEffort = (value != null && !value.isEmpty());
		}
		catch (final NumberFormatException e) {
			declaredEffort = 0;
			hasDeclaredEffort = false;
		}

		return new ScopeDeclareEffortAction(scope.getId(), hasDeclaredEffort, declaredEffort);
	}

}
