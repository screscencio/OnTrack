package br.com.oncast.ontrack.shared.services.actionExecution;

import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public class ReleaseActionExecuter implements ModelActionExecuter {

	// Constructor must be package visible.
	ReleaseActionExecuter() {}

	@Override
	public ActionExecutionContext executeAction(final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		final ModelAction reverseAction = action.execute(context);
		return new ActionExecutionContext(reverseAction);
	}

}
