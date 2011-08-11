package br.com.oncast.ontrack.server.business;

import java.util.Date;
import java.util.List;

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

public class BusinessLogic {

	private final PersistenceService persistenceService;
	private final ActionBroadcastService actionBroadcastService;

	protected BusinessLogic(final PersistenceService persistenceService, final ActionBroadcastService actionBroadcastService) {
		this.persistenceService = persistenceService;
		this.actionBroadcastService = actionBroadcastService;
	}

	public void handleIncomingAction(final ModelAction action) throws UnableToHandleActionException {
		try {
			validateIncomingAction(action);
			persistenceService.persistAction(action, new Date());
			actionBroadcastService.broadcast(action);
		}
		catch (final PersistenceException e) {
			// TODO ++Log original exception and throw a new one that can be shared with GWT code.
			throw new UnableToHandleActionException("The server could not process the action.", e);
		}
	}

	// TODO Report errors as feedback for development.
	// TODO Re-think validations strategy as loading the project every time may be a performance bottleneck.
	// DECISION It is common sense that this validation is needed at this time (of development). Roberto thinks it should be a provisory solution, as it may be
	// a major performance bottleneck, but that the solution is good to ensure that BetaTesters are safe while the application matures. Rodrigo thinks it should
	// be permanent (but maybe passive of refactorings) to guarantee user safety in the long term.
	private void validateIncomingAction(final ModelAction action) throws UnableToHandleActionException {
		try {
			final Project project = loadProject();
			final ProjectContext context = new ProjectContext(project);
			ActionExecuter.executeAction(context, action);
		}
		catch (final UnableToCompleteActionException e) {
			throw new InvalidIncomingAction(e);
		}
		catch (final UnableToLoadProjectException e) {
			throw new UnableToHandleActionException("Unable to validate action.", e);
		}
	}

	// TODO Persist new snapshot after restoring the project correctly.
	public Project loadProject() throws UnableToLoadProjectException {
		try {
			final ProjectSnapshot snapshot = persistenceService.retrieveProjectSnapshot();
			final List<ModelAction> actionList = persistenceService.retrieveActionsSince(snapshot.getTimestamp());
			return applyActionsToProjectSnapshot(snapshot, actionList);
		}
		catch (final PersistenceException e) {
			// TODO ++Log original exception and throw a new one that can be shared with GWT code.
			throw new UnableToLoadProjectException("The server could not load the project: A persistence exception occured.", e);
		}
		catch (final UnableToCompleteActionException e) {
			// TODO ++Log original exception and throw a new one that can be shared with GWT code.
			throw new UnableToLoadProjectException("The server could not load the project: The project state could not be correctly restored.", e);
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
