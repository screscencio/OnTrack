package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

import java.util.Date;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class InsertChildInternalAction implements TwoStepInternalAction {

	private final Scope scope;
	private ScopeTreeItem newTreeItem;
	private ScopeTreeItem selectedTreeItem;

	public InsertChildInternalAction(final Scope scope) {
		this.scope = scope;
	}

	@Override
	public void execute(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		selectedTreeItem = InternalActionHelper.findScopeTreeItem(tree, scope);
		selectedTreeItem.setState(true);

		newTreeItem = new ScopeTreeItem(new Scope("", InternalActionHelper.findCurrentUser(), new Date()));

		selectedTreeItem.addItem(newTreeItem);
		if (!selectedTreeItem.getState()) selectedTreeItem.setState(true, false);

		tree.setSelectedItem(newTreeItem, false);
		newTreeItem.enterEditMode();
	}

	@Override
	public void rollback(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		newTreeItem.remove();
		tree.setSelectedItem(selectedTreeItem, false);
	}

	@Override
	public ModelAction createEquivalentModelAction(final String value) {
		return new ScopeInsertChildAction(scope.getId(), value);
	}

}
