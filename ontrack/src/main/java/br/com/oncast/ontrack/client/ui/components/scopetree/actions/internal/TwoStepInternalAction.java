package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;

public interface TwoStepInternalAction extends InternalAction {

	public void rollback(final ScopeTreeWidget tree) throws UnableToCompleteActionException;

	public ModelAction createEquivalentModelAction(final String value);
}