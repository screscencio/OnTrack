package br.com.oncast.ontrack.server.business;

import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

public interface BusinessLogic {

	public abstract void handleIncomingAction(final ModelActionSyncRequest modelActionSyncRequest) throws UnableToHandleActionException;

	public abstract Project loadProject() throws UnableToLoadProjectException;
}