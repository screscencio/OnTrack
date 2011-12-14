package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

class ReleaseActionHelper {

	public static Release findRelease(final UUID referenceId, final ProjectContext context) throws UnableToCompleteActionException {
		try {
			return context.findRelease(referenceId);
		}
		catch (final ReleaseNotFoundException e) {
			throw new UnableToCompleteActionException(e);
		}
	}

	public static Release findRelease(final String releaseDescription, final ProjectContext context) throws UnableToCompleteActionException {
		try {
			return context.findRelease(releaseDescription);
		}
		catch (final ReleaseNotFoundException e) {
			throw new UnableToCompleteActionException(e);
		}
	}

}
