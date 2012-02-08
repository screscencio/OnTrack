package br.com.oncast.ontrack.shared.services.actionExecution;

import br.com.oncast.ontrack.shared.model.action.KanbanAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public class ActionExecuter {

	public static ActionExecutionContext executeAction(final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		if (action instanceof ScopeAction) return new ScopeActionExecuter().executeAction(context, action);
		if (action instanceof ReleaseAction) return new ReleaseActionExecuter().executeAction(context, action);
		if (action instanceof KanbanAction) return new KanbanActionExecuter().executeAction(context, action);

		throw new UnableToCompleteActionException("There is no mapped action executer for the type " + action.getClass() + ".");
	}
}
