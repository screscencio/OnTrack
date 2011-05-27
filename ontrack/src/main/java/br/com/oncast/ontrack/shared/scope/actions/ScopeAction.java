package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public interface ScopeAction {

	void execute(final ProjectContext context) throws UnableToCompleteActionException;

	void rollback(final ProjectContext context) throws UnableToCompleteActionException;

	Scope getScope();
}
