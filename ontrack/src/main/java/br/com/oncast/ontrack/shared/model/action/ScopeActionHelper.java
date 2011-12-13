package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

class ScopeActionHelper {

	public static Scope findScope(final UUID referenceId, final ProjectContext context) throws UnableToCompleteActionException {
		try {
			return context.findScope(referenceId);
		}
		catch (final ScopeNotFoundException e) {
			throw new UnableToCompleteActionException(e);
		}
	}

}
