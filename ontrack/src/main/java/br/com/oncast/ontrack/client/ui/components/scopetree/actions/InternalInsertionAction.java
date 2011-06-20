package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public interface InternalInsertionAction {

	public void execute(ScopeTreeItem treeItem) throws UnableToCompleteActionException;

	public void rollback() throws UnableToCompleteActionException;

	public ModelAction createEquivalentModelAction(final String descriptivePattern);
}
