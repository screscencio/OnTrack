package br.com.oncast.ontrack.server.business;

import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToAuthorizeUserException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToRemoveAuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.business.ProjectNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateProjectRepresentation;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRetrieveProjectListException;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.project.ProjectRevision;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncEventRequestResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;

// TODO Analyze dividing this class into multiple classes, each doing a specific job.
public interface BusinessLogic {

	public abstract long handleIncomingActionSyncRequest(final ModelActionSyncRequest modelActionSyncRequest) throws UnableToHandleActionException,
			AuthorizationException;

	public abstract ProjectRevision loadProjectForClient(final ProjectContextRequest projectContextRequest) throws UnableToLoadProjectException,
			ProjectNotFoundException;

	public abstract ProjectRepresentation createProject(final String projectName) throws UnableToCreateProjectRepresentation, PersistenceException,
			AuthorizationException;

	public abstract List<ProjectRepresentation> retrieveCurrentUserProjectList() throws UnableToRetrieveProjectListException;

	public ProjectRevision loadProject(UUID uuid) throws ProjectNotFoundException, UnableToLoadProjectException;

	public abstract void sendProjectCreationQuotaRequestEmail();

	public abstract void sendFeedbackEmail(String feedbackText);

	void authorize(String userEmail, UUID projectId, boolean wasRequestedByTheUser) throws UnableToAuthorizeUserException, UnableToHandleActionException,
			AuthorizationException;

	public abstract void onFileUploadCompleted(final FileRepresentation fileRepresentation) throws UnableToHandleActionException, AuthorizationException;

	public abstract void loadProjectForMigration(UUID projectId) throws ProjectNotFoundException, UnableToLoadProjectException;

	public abstract void removeAuthorization(UUID userId, UUID projectId) throws UnableToHandleActionException, UnableToRemoveAuthorizationException,
			PersistenceException, AuthorizationException;

	public abstract ModelActionSyncEventRequestResponse loadProjectActions(UUID projectId, long lastSyncId) throws AuthorizationException,
			UnableToLoadProjectException;

	public abstract UUID createUser(String email);

}