package br.com.oncast.ontrack.server.business;

import java.util.List;

import br.com.oncast.ontrack.shared.exceptions.business.ProjectNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToAuthorizeUserException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateProjectRepresentation;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRetrieveProjectListException;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;

// TODO Analyze dividing this class into multiple classes, each doing a specific job.
public interface BusinessLogic {

	public abstract void handleIncomingActionSyncRequest(final ModelActionSyncRequest modelActionSyncRequest) throws UnableToHandleActionException;

	public abstract Project loadProjectForClient(final ProjectContextRequest projectContextRequest) throws UnableToLoadProjectException,
			ProjectNotFoundException;

	public abstract ProjectRepresentation createProject(final String projectName) throws UnableToCreateProjectRepresentation;

	// TODO Delete the following method.
	public abstract List<ProjectRepresentation> retrieveProjectList() throws UnableToRetrieveProjectListException;

	public abstract List<ProjectRepresentation> retrieveCurrentUserProjectList() throws UnableToRetrieveProjectListException;

	public Project loadProject(long projectId) throws ProjectNotFoundException, UnableToLoadProjectException;

	void authorize(long projectId, String userEmail, boolean sendMailNotification) throws UnableToAuthorizeUserException;
}