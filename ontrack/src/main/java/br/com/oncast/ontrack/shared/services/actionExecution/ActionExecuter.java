package br.com.oncast.ontrack.shared.services.actionExecution;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ReleaseAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class ActionExecuter {

	public static ActionExecutionContext executeAction(final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		if (action instanceof ScopeAction) return new ScopeActionExecuter().executeAction(context, action);
		if (action instanceof ReleaseAction) return new ReleaseActionExecuter().executeAction(context, action);

		throw new UnableToCompleteActionException("There is no mapped action executer for the type " + action.getClass() + ".");
	}
}
