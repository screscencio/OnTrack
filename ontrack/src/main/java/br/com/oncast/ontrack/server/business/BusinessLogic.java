package br.com.oncast.ontrack.server.business;

import java.util.List;

import br.com.oncast.ontrack.shared.exceptions.business.ProjectNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateProjectRepresentation;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRetrieveProjectListException;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

// TODO Analyze dividing this class into multiple classes, each doing a specific job.
public interface BusinessLogic {

	public abstract void handleIncomingActionSyncRequest(final ModelActionSyncRequest modelActionSyncRequest) throws UnableToHandleActionException;

	public abstract Project loadProject(final long projectId) throws UnableToLoadProjectException, ProjectNotFoundException;

	public abstract ProjectRepresentation createProject(final String projectName) throws UnableToCreateProjectRepresentation;

	public abstract List<ProjectRepresentation> retrieveProjectList() throws UnableToRetrieveProjectListException;
}