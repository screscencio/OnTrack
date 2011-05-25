package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeNotFoundException;

public interface ScopeTreeActionFactory {

	public abstract ScopeTreeAction createEquivalentActionFor(final ScopeAction action) throws ScopeNotFoundException;

}