package br.com.oncast.ontrack.server.business;

import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;

// TODO Analyze dividing this class into multiple classes, each doing a specific job.
public interface BusinessLogic {

	public abstract void handleIncomingActionSyncRequest(final ModelActionSyncRequest modelActionSyncRequest) throws UnableToHandleActionException;

	public abstract Project loadProject(final ProjectContextRequest projectContextRequest) throws UnableToLoadProjectException;
}