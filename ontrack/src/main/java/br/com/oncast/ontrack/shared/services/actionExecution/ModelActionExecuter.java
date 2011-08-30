package br.com.oncast.ontrack.shared.services.actionExecution;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public interface ModelActionExecuter {
	ActionExecutionContext executeAction(final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException;
}
