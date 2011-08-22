package br.com.oncast.ontrack.server.business;

import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.Project;

public interface BusinessLogic {

	public abstract void handleIncomingAction(final ModelAction action) throws UnableToHandleActionException;

	public abstract Project loadProject() throws UnableToLoadProjectException;
}