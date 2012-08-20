package br.com.oncast.ontrack.server.business;

import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToAuthorizeUserException;
import br.com.oncast.ontrack.shared.exceptions.business.ProjectNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateProjectRepresentation;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRetrieveProjectListException;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;

// TODO Analyze dividing this class into multiple classes, each doing a specific job.
public interface BusinessLogic {

	public abstract void handleIncomingActionSyncRequest(final ModelActionSyncRequest modelActionSyncRequest) throws UnableToHandleActionException,
			AuthorizationException;

	public abstract Project loadProjectForClient(final ProjectContextRequest projectContextRequest) throws UnableToLoadProjectException,
			ProjectNotFoundException;

	public abstract ProjectRepresentation createProject(final String projectName) throws UnableToCreateProjectRepresentation, PersistenceException,
			AuthorizationException;

	public abstract List<ProjectRepresentation> retrieveCurrentUserProjectList() throws UnableToRetrieveProjectListException;

	public Project loadProject(UUID uuid) throws ProjectNotFoundException, UnableToLoadProjectException;

	public abstract void sendProjectCreationQuotaRequestEmail();

	public abstract void sendFeedbackEmail(String feedbackText);

	void authorize(String userEmail, UUID projectId, boolean wasRequestedByTheUser) throws UnableToAuthorizeUserException, UnableToHandleActionException,
			AuthorizationException;

	public abstract void onFileUploadCompleted(final FileRepresentation fileRepresentation) throws UnableToHandleActionException, AuthorizationException;

	public abstract void loadProjectForMigration(UUID projectId) throws ProjectNotFoundException, UnableToLoadProjectException;

}