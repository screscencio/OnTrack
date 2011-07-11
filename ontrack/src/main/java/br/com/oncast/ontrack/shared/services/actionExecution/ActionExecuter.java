package br.com.oncast.ontrack.shared.services.actionExecution;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class ActionExecuter {

	public static ModelAction executeAction(final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		final Scope scope = getEffortInferenceBaseScope(context, action);
		final ModelAction reverseAction = action.execute(context);
		if (action instanceof ScopeAction) {
			if (((ScopeAction) action).changesEffortInference()) {
				EffortInferenceEngine.process(scope);
			}
		}
		return reverseAction;
	}

	private static Scope getEffortInferenceBaseScope(final ProjectContext context, final ModelAction action) {
		final Scope s = context.findScope(action.getReferenceId());
		final Scope scope = s.isRoot() ? s : s.getParent();
		return scope;
	}

}
