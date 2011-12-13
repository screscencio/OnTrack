package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertParentAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class InsertFatherInternalAction implements InternalAction {

	private final Scope scope;
	private ScopeTreeItem newTreeItem;
	private ScopeTreeItem selectedTreeItem;

	public InsertFatherInternalAction(final Scope scope) {
		this.scope = scope;
	}

	@Override
	public void execute(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		selectedTreeItem = InternalActionHelper.findScopeTreeItem(tree, scope);
		newTreeItem = new ScopeTreeItem(new Scope(""));

		final ScopeTreeItem parentTreeItem = selectedTreeItem.getParentItem();
		parentTreeItem.insertItem(parentTreeItem.getChildIndex(selectedTreeItem), newTreeItem);
		parentTreeItem.removeItem(selectedTreeItem);
		newTreeItem.addItem(selectedTreeItem);

		tree.setSelectedItem(newTreeItem);
		newTreeItem.setState(true);
		newTreeItem.enterEditMode();
	}

	@Override
	public void rollback(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		final ScopeTreeItem parentTreeItem = newTreeItem.getParentItem();
		parentTreeItem.insertItem(parentTreeItem.getChildIndex(newTreeItem), selectedTreeItem);
		newTreeItem.remove();

		tree.setSelected(selectedTreeItem);
	}

	@Override
	public ModelAction createEquivalentModelAction(final String value) {
		return new ScopeInsertParentAction(scope.getId(), value);
	}

}
