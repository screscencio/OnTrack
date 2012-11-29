package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

import java.util.Date;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingDownAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

public class InsertSiblingDownInternalAction implements TwoStepInternalAction {

	private ScopeTreeItem newTreeItem;
	private final Scope scope;
	private ScopeTreeItem treeItem;

	public InsertSiblingDownInternalAction(final Scope scope) {
		this.scope = scope;
	}

	@Override
	public void execute(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		treeItem = InternalActionHelper.findScopeTreeItem(tree, scope);
		if (treeItem.isRoot()) throw new UnableToCompleteActionException(ActionExecutionErrorMessageCode.CREATE_ROOT_SIBLING);
		final UserRepresentation user = new UserRepresentation(ClientServiceProvider.getInstance().getAuthenticationService().getCurrentUser().getId());

		newTreeItem = new ScopeTreeItem(new Scope("", user, new Date()));

		final ScopeTreeItem parentItem = treeItem.getParentItem();
		parentItem.insertItem(parentItem.getChildIndex(treeItem) + 1, newTreeItem);

		tree.setSelectedItem(newTreeItem, false);
		newTreeItem.enterEditMode();
	}

	@Override
	public void rollback(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		newTreeItem.remove();
		tree.setSelectedItem(treeItem, false);
	}

	@Override
	public ModelAction createEquivalentModelAction(final String value) {
		return new ScopeInsertSiblingDownAction(scope.getId(), value);
	}
}
