package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public interface InternalAction {

	public void execute(ScopeTreeWidget tree) throws UnableToCompleteActionException;

	public void rollback() throws UnableToCompleteActionException;

	public ModelAction createEquivalentModelAction(final String value);
}
