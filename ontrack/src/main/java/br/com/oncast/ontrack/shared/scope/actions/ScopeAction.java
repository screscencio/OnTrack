package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public interface ScopeAction {

	void execute() throws UnableToCompleteActionException;

	void rollback() throws UnableToCompleteActionException;

	Scope getScope();
}
