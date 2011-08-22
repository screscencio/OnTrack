package br.com.oncast.ontrack.server.business;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.services.actionSync.ActionBroadcastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.InvalidIncomingAction;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuter;

class BusinessLogicImpl implements BusinessLogic {

	private static final Logger LOGGER = Logger.getLogger(BusinessLogicImpl.class);

	private final PersistenceService persistenceService;
	private final ActionBroadcastService actionBroadcastService;

	protected BusinessLogicImpl(final PersistenceService persistenceService, final ActionBroadcastService actionBroadcastService) {
		this.persistenceService = persistenceService;
		this.actionBroadcastService = actionBroadcastService;
	}

	/**
	 * @see br.com.oncast.ontrack.server.business.BusinessLogic#handleIncomingAction(br.com.oncast.ontrack.shared.model.actions.ModelAction)
	 */
	@Override
	public void handleIncomingAction(final ModelAction action) throws UnableToHandleActionException {
		LOGGER.debug("Processing incoming action '" + action.getClass().getSimpleName() + "'");
		try {
			validateAndPersistIncomingAction(action);
			actionBroadcastService.broadcast(action);
		}
		catch (final PersistenceException e) {
			final String errorMessage = "The server could not handle the incoming action correctly. The action could not be persisted.";
			LOGGER.error(errorMessage, e);
			throw new UnableToHandleActionException(errorMessage);
		}
	}

	private synchronized void validateAndPersistIncomingAction(final ModelAction action) throws UnableToHandleActionException, PersistenceException {
		validateIncomingAction(action);
		persistenceService.persistAction(action, new Date());
	}

	// TODO Report errors as feedback for development.
	// TODO Re-think validations strategy as loading the project every time may be a performance bottleneck.
	// DECISION It is common sense that this validation is needed at this time (of development). Roberto thinks it should be a provisory solution, as it may be
	// a major performance bottleneck, but that the solution is good to ensure that BetaTesters are safe while the application matures. Rodrigo thinks it should
	// be permanent (but maybe passive of refactorings) to guarantee user safety in the long term.
	private void validateIncomingAction(final ModelAction action) throws UnableToHandleActionException {
		LOGGER.debug("Validating action upon the project current state.");
		try {
			final Project project = loadProject();
			final ProjectContext context = new ProjectContext(project);
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
	}

	// TODO Persist new snapshot after restoring the project correctly.
	/**
	 * @see br.com.oncast.ontrack.server.business.BusinessLogic#loadProject()
	 */
	@Override
	public Project loadProject() throws UnableToLoadProjectException {
		LOGGER.debug("Loading project current state.");
		try {
			final ProjectSnapshot snapshot = persistenceService.retrieveProjectSnapshot();
			final List<ModelAction> actionList = persistenceService.retrieveActionsSince(snapshot.getTimestamp());
			return applyActionsToProjectSnapshot(snapshot, actionList);
		}
		catch (final PersistenceException e) {
			final String errorMessage = "The server could not load the project: A persistence exception occured.";
			LOGGER.error(errorMessage, e);
			throw new UnableToLoadProjectException(errorMessage);
		}
		catch (final UnableToCompleteActionException e) {
			final String errorMessage = "The server could not load the project: The project state could not be correctly restored.";
			LOGGER.error(errorMessage, e);
			throw new UnableToLoadProjectException(errorMessage);
		}
	}

	private Project applyActionsToProjectSnapshot(final ProjectSnapshot snapshot, final List<ModelAction> actionList) throws UnableToCompleteActionException {
		final Project project = snapshot.getProject();
		final ProjectContext context = new ProjectContext(project);

		for (final ModelAction action : actionList)
			ActionExecuter.executeAction(context, action);

		return project;
	}

}
