package br.com.oncast.ontrack.server.business;

import java.util.Date;
import java.util.List;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.exceptions.persistence.PersistenceException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuter;

public class BusinessLogic {

	private final PersistenceService persistenceService;

	public BusinessLogic(final PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	public void handleIncomingAction(final ModelAction action) throws UnableToHandleActionException {
		try {
			persistenceService.persistAction(action, new Date());
		}
		catch (final PersistenceException e) {
			throw new UnableToHandleActionException("The server could not process the action.", e);
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
			throw new UnableToLoadProjectException("The server could not load the project: An persistence exception occured.", e);
		}
		catch (final UnableToCompleteActionException e) {
			throw new UnableToLoadProjectException("The server could not load the project: The project state could not be correctly restored.", e);
		}
	}

	private Project applyActionsToProjectSnapshot(final ProjectSnapshot snapshot, final List<ModelAction> actionList) throws UnableToCompleteActionException {
		final Project project = snapshot.getProject();
		final ProjectContext context = new ProjectContext(project);

		for (final ModelAction action : actionList) {
			ActionExecuter.executeAction(context, action);
		}

		return project;
	}

}
