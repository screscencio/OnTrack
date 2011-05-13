package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.shared.beans.Scope;

public class InsertChildScopeTreeWidgetAction implements ScopeTreeWidgetAction {

	private final ScopeTreeItem parentTreeItem;
	private final Scope referencedScope;

	public InsertChildScopeTreeWidgetAction(final ScopeTreeItem parentTreeItem, final Scope referencedScope) {
		this.parentTreeItem = parentTreeItem;
		this.referencedScope = referencedScope;
	}

	@Override
	public void execute() throws UnableToCompleteActionException {
		final ScopeTreeItem newItem = new ScopeTreeItem(referencedScope);
		parentTreeItem.addItem(newItem);

		parentTreeItem.setState(true);
		newItem.setSelected(true);
		newItem.enterEditMode();
	}

	@Override
	public void rollback() throws UnableToCompleteActionException {
		final int childCount = parentTreeItem.getChildCount();
		if (childCount == 0) throw new UnableToCompleteActionException("The action cannot be undone because an inconsistence was found.");

		new RemoveScopeTreeWidgetAction(parentTreeItem.getChild(childCount - 1)).execute();
	}
}
