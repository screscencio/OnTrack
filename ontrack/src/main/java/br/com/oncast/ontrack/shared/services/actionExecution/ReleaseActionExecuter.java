package br.com.oncast.ontrack.shared.services.actionExecution;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class ReleaseActionExecuter implements ModelActionExecuter {

	// Constructor must be package visible.
	ReleaseActionExecuter() {}

	@Override
	public ActionExecutionContext executeAction(final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		final ModelAction reverseAction = action.execute(context);
		return new ActionExecutionContext(reverseAction);
	}

}
