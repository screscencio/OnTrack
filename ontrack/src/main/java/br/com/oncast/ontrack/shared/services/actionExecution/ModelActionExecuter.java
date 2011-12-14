package br.com.oncast.ontrack.shared.services.actionExecution;

import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public interface ModelActionExecuter {
	ActionExecutionContext executeAction(final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException;
}
