package br.com.oncast.ontrack.server.business;

import java.util.Date;
import java.util.List;

import br.com.oncast.ontrack.server.business.exception.BusinessException;
import br.com.oncast.ontrack.server.business.exception.UnableToHandleAction;
import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.services.persistence.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class BusinessLogic {

	private final PersistenceService persistenceService;

	public BusinessLogic(final PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	public void handleIncomingAction(final ModelAction action) throws BusinessException {
		try {
			persistenceService.persist(action, new Date());
		}
		catch (final PersistenceException e) {
			throw new UnableToHandleAction(e);
		}
	}

	public Project loadProject() throws BusinessException {
		ProjectSnapshot snapshot;
		try {
			snapshot = persistenceService.retrieveProjectSnapshot();
			final List<ModelAction> actionList = persistenceService.retrieveActionsSince(snapshot.getTimestamp());
			return executeActions(actionList, snapshot.getProject());
		}
		catch (final Exception e) {
			throw new BusinessException("There was not possible to load the project.", e);
		}
	}

	private Project executeActions(final List<ModelAction> actionList, final Project project) throws UnableToCompleteActionException {
		for (final ModelAction action : actionList) {
			action.execute(new ProjectContext(project));
		}
		return null;
	}

}
