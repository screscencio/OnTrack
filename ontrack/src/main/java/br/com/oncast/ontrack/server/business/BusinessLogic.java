package br.com.oncast.ontrack.server.business;

import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToAuthorizeUserException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToRemoveAuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.business.ProjectNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateProjectRepresentationException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRemoveProjectRepresentationException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRetrieveProjectListException;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.project.ProjectRevision;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncEventRequestResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;

import java.util.List;

// TODO Analyze dividing this class into multiple classes, each doing a specific job.
public interface BusinessLogic {

	long handleIncomingActionSyncRequest(final ModelActionSyncRequest modelActionSyncRequest) throws UnableToHandleActionException, AuthorizationException;

	ProjectRevision loadProjectForClient(final ProjectContextRequest projectContextRequest) throws UnableToLoadProjectException, ProjectNotFoundException;

	ProjectRepresentation createProject(final String projectName) throws UnableToCreateProjectRepresentationException;

	ProjectRepresentation removeProject(final UUID projectId) throws UnableToRemoveProjectRepresentationException;

	List<ProjectRepresentation> retrieveCurrentUserProjectList() throws UnableToRetrieveProjectListException;

	ProjectRevision loadProject(UUID uuid) throws ProjectNotFoundException, UnableToLoadProjectException;

	void sendProjectCreationQuotaRequestEmail();

	void sendFeedbackEmail(String feedbackText);

	void authorize(String userEmail, UUID projectId, boolean isSuperUser, boolean wasRequestedByTheUser) throws UnableToAuthorizeUserException, UnableToHandleActionException, AuthorizationException;

	void onFileUploadCompleted(final FileRepresentation fileRepresentation) throws UnableToHandleActionException, AuthorizationException;

	void loadProjectForMigration(UUID projectId) throws ProjectNotFoundException, UnableToLoadProjectException;

	void removeAuthorization(UUID userId, UUID projectId) throws UnableToHandleActionException, UnableToRemoveAuthorizationException, PersistenceException, AuthorizationException;

	ModelActionSyncEventRequestResponse loadProjectActions(UUID projectId, long lastSyncId) throws AuthorizationException, UnableToLoadProjectException;

	UUID createUser(String userEmail, boolean isSuperUser);

}