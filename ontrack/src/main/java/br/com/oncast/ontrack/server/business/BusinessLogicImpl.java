package br.com.oncast.ontrack.server.business;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.broadcast.BroadcastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.InvalidIncomingAction;
import br.com.oncast.ontrack.shared.exceptions.business.ProjectNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateProjectRepresentation;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRetrieveProjectListException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuter;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

class BusinessLogicImpl implements BusinessLogic {

	private static final Logger LOGGER = Logger.getLogger(BusinessLogicImpl.class);

	private final PersistenceService persistenceService;
	private final BroadcastService broadcastService;

	protected BusinessLogicImpl(final PersistenceService persistenceService, final BroadcastService actionBroadcastService) {
		this.persistenceService = persistenceService;
		this.broadcastService = actionBroadcastService;
	}

	@Override
	public void handleIncomingActionSyncRequest(final ModelActionSyncRequest modelActionSyncRequest) throws UnableToHandleActionException {
		LOGGER.debug("Processing incoming action batch.");
		try {
			final List<ModelAction> actionList = modelActionSyncRequest.getActionList();
			final long projectId = modelActionSyncRequest.getProjectId();
			synchronized (this) {
				validateIncomingActions(projectId, actionList);
				postProcessIncomingActions(actionList);
				persistenceService.persistActions(projectId, actionList, new Date());
			}
			broadcastService.broadcastActionSyncRequest(modelActionSyncRequest);
		}
		catch (final PersistenceException e) {
			final String errorMessage = "The server could not handle the incoming action correctly. The action could not be persisted.";
			LOGGER.error(errorMessage, e);
			throw new UnableToHandleActionException(errorMessage);
		}
	}

	// TODO Report errors as feedback for development.
	// TODO Re-think validation strategy as loading the project every time may be a performance bottleneck.
	// DECISION It is common sense that this validation is needed at this time (of development). Roberto thinks it should be a provisory solution, as it may be
	// a major performance bottleneck, but that the solution is good to ensure that BetaTesters are safe while the application matures. Rodrigo thinks it should
	// be permanent (but maybe passive of refactorings) to guarantee user safety in the long term.
	private void validateIncomingActions(final long projectId, final List<ModelAction> actionList) throws UnableToHandleActionException {
		LOGGER.debug("Validating action upon the project current state.");
		try {
			final Project project = loadProject(projectId);
			final ProjectContext context = new ProjectContext(project);
			for (final ModelAction action : actionList)
				ActionExecuter.executeAction(context, action);
		}
		catch (final UnableToCompleteActionException e) {
			final String errorMessage = "Unable to process action. The incoming action is invalid.";
			LOGGER.debug(errorMessage, e);
			throw new InvalidIncomingAction(errorMessage);
		}
		catch (final UnableToLoadProjectException e) {
			final String errorMessage = "The server could not handle the incoming action. The action could not be validated because the project could not be loaded.";
			LOGGER.error(errorMessage, e);
			throw new UnableToHandleActionException(errorMessage);
		}
		catch (final Exception e) {
			final String errorMessage = "Unable to process action. An unknown problem occured.";
			LOGGER.debug(errorMessage, e);
			throw new InvalidIncomingAction(errorMessage);
		}
	}

	// TODO Find a better way to post process actions. (Eg. ScopeDeclareProgressAction come from clients with its own time definitions, and this time stamps
	// should be standardized using the server time).
	private void postProcessIncomingActions(final List<ModelAction> actionList) {
		LOGGER.debug("Post-Processing actions.");
		for (final ModelAction action : actionList) {
			if (action instanceof ScopeDeclareProgressAction) ((ScopeDeclareProgressAction) action).setTimestamp(new Date());
		}
	}

	// FIXME Test this method
	@Override
	public ProjectRepresentation createProject(final String projectName) throws UnableToCreateProjectRepresentation {
		LOGGER.debug("Creating new project '" + projectName + "'.");
		try {
			final ProjectRepresentation projectRepresentation = new ProjectRepresentation(projectName);
			final ProjectRepresentation persistedProjectRepresentation = persistenceService.persistOrUpdateProjectRepresentation(projectRepresentation);
			broadcastService.broadcastProjectCreation(persistedProjectRepresentation);
			return persistedProjectRepresentation;
		}
		catch (final PersistenceException e) {
			final String errorMessage = "Unable to create project '" + projectName + "'.";
			LOGGER.debug(errorMessage, e);
			throw new UnableToCreateProjectRepresentation(errorMessage);
		}
	}

	@Override
	public List<ProjectRepresentation> retrieveProjectList() throws UnableToRetrieveProjectListException {
		LOGGER.debug("Retrieving project list.");
		try {
			return persistenceService.retrieveAllProjectRepresentations();
		}
		catch (final PersistenceException e) {
			final String errorMessage = "Unable to retrieve the project list.";
			LOGGER.debug(errorMessage, e);
			throw new UnableToRetrieveProjectListException(errorMessage);
		}
	}

	@Override
	public synchronized Project loadProject(final long projectId) throws UnableToLoadProjectException, ProjectNotFoundException {
		LOGGER.debug("Loading current state for project id '" + projectId + "'.");
		try {
			final ProjectSnapshot snapshot = loadProjectSnapshot(projectId);
			final List<UserAction> actionList = persistenceService.retrieveActionsSince(projectId, snapshot.getLastAppliedActionId());

			Project project = snapshot.getProject();
			if (actionList.isEmpty()) return project;

			project = applyActionsToProject(project, actionList);
			updateProjectSnapshot(snapshot, project, actionList.get(actionList.size() - 1).getId());

			return project;
		}
		catch (final NoResultFoundException e) {
			final String errorMessage = "The server could not load the project: The project is inexistent.";
			LOGGER.error(errorMessage, e);
			throw new ProjectNotFoundException(errorMessage);
		}
		catch (final PersistenceException e) {
			final String errorMessage = "The server could not load the project: A persistence exception occured.";
			LOGGER.error(errorMessage, e);
			throw new UnableToLoadProjectException(errorMessage);
		}
		catch (final UnableToCompleteActionException e) {
			final String errorMessage = "The server could not load the project. The project state could not be correctly restored.";
			LOGGER.error(errorMessage, e);
			throw new UnableToLoadProjectException(errorMessage);
		}
		catch (final Exception e) {
			final String errorMessage = "The server could not load the project. The project state could not be correctly restored because of an unknown problem.";
			LOGGER.error(errorMessage, e);
			throw new UnableToLoadProjectException(errorMessage);
		}
	}

	private ProjectSnapshot loadProjectSnapshot(final long projectId) throws PersistenceException, UnableToLoadProjectException, NoResultFoundException {
		ProjectSnapshot snapshot;
		try {
			snapshot = persistenceService.retrieveProjectSnapshot(projectId);
		}
		catch (final NoResultFoundException e) {
			snapshot = createBlankProjectSnapshot(projectId);
		}
		return snapshot;
	}

	private ProjectSnapshot createBlankProjectSnapshot(final long projectId) throws UnableToLoadProjectException, NoResultFoundException, PersistenceException {
		try {
			final ProjectRepresentation projectRepresentation = persistenceService.retrieveProjectRepresentation(projectId);

			final Scope projectScope = new Scope(projectRepresentation.getName(), new UUID("0"));
			final Release projectRelease = new Release(projectRepresentation.getName(), new UUID("release0"));

			final ProjectSnapshot projectSnapshot = new ProjectSnapshot(new Project(projectRepresentation, projectScope,
					projectRelease), new Date());
			return projectSnapshot;
		}
		catch (final IOException e) {
			final String errorMessage = "It was not possible to create a blank project snapshot.";
			LOGGER.error(errorMessage, e);
			throw new UnableToLoadProjectException(errorMessage);
		}
	}

	private void updateProjectSnapshot(final ProjectSnapshot snapshot, final Project project, final long lastAppliedActionId) throws IOException,
			PersistenceException {

		snapshot.setProject(project);
		snapshot.setTimestamp(new Date());
		snapshot.setLastAppliedActionId(lastAppliedActionId);

		persistenceService.persistProjectSnapshot(snapshot);
	}

	private Project applyActionsToProject(final Project project, final List<UserAction> actionList) throws UnableToCompleteActionException {
		final ProjectContext context = new ProjectContext(project);

		for (final UserAction action : actionList)
			ActionExecuter.executeAction(context, action.getModelAction());

		return project;
	}
}
