package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.beans.Scope;

public interface ScopeAction {

	void execute() throws UnableToCompleteActionException;

	void rollback() throws UnableToCompleteActionException;

	Scope getScope();
}
