package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;

public interface ScopeTreeWidgetActionFactory {

	public abstract ScopeTreeWidgetAction getEquivalentActionFor(final ScopeAction action) throws UnableToCompleteActionException;

}