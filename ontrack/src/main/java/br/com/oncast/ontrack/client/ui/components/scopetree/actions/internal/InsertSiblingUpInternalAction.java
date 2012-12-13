package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

import java.util.Date;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingUpAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class InsertSiblingUpInternalAction implements TwoStepInternalAction {

	private ScopeTreeItem newTreeItem;
	private final Scope scope;
	private ScopeTreeItem treeItem;

	public InsertSiblingUpInternalAction(final Scope scope) {
		this.scope = scope;
	}

	@Override
	public void execute(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		treeItem = InternalActionHelper.findScopeTreeItem(tree, scope);
		if (treeItem.isRoot()) throw new UnableToCompleteActionException(ActionExecutionErrorMessageCode.CREATE_ROOT_SIBLING);
		newTreeItem = new ScopeTreeItem(new Scope("", ClientServiceProvider.getCurrentUser(), new Date()));

		final ScopeTreeItem parentItem = treeItem.getParentItem();
		parentItem.insertItem(parentItem.getChildIndex(treeItem), newTreeItem);

		newTreeItem.getTree().setSelectedItem(newTreeItem);
		newTreeItem.enterEditMode();
	}

	@Override
	public void rollback(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		newTreeItem.remove();
		tree.setSelectedItem(treeItem, false);
	}

	@Override
	public ModelAction createEquivalentModelAction(final String value) {
		return new ScopeInsertSiblingUpAction(scope.getId(), value);
	}

}
