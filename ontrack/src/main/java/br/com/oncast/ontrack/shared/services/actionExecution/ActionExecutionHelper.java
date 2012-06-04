package br.com.oncast.ontrack.shared.services.actionExecution;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public final class ActionExecutionHelper {

	public static List<ModelAction> executeSubActions(final List<ModelAction> subActions, final ProjectContext context, final ActionContext actionContext)
			throws UnableToCompleteActionException {
		final List<ModelAction> rollbackSubActions = new ArrayList<ModelAction>();
		for (final ModelAction action : subActions) {
			rollbackSubActions.add(0, action.execute(context, actionContext));
		}
		return rollbackSubActions;
	}
}
