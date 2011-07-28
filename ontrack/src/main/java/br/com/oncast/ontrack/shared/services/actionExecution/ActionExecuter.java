package br.com.oncast.ontrack.shared.services.actionExecution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertParentRollbackAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.inference.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.inference.InferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.inference.ProgressInferenceEngine;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ActionExecuter {

	private static final List<InferenceEngine> inferenceEngines = new ArrayList<InferenceEngine>();

	static {
		inferenceEngines.add(new EffortInferenceEngine());
		inferenceEngines.add(new ProgressInferenceEngine());
	}

	public static ModelAction executeAction(final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		return executeAction(context, action, null);
	}

	public static ModelAction executeAction(final ProjectContext context, final ModelAction action, final ActionExecutionListener listener)
			throws UnableToCompleteActionException {
		final Scope scope = getEffortInferenceBaseScope(context, action);

		final ModelAction reverseAction = action.execute(context);
		final Set<UUID> inferenceInfluencedScopeSet = executeInferenceEngines(action, scope);
		if (listener != null) listener.onActionExecution(action, context, inferenceInfluencedScopeSet);

		return reverseAction;
	}

	private static Set<UUID> executeInferenceEngines(final ModelAction action, final Scope scope) {
		final Set<UUID> inferenceInfluencedScopeSet = new HashSet<UUID>();
		for (final InferenceEngine inferenceEngine : inferenceEngines)
			if (inferenceEngine.shouldProcess(action)) inferenceInfluencedScopeSet.addAll(inferenceEngine.process(scope));
		return inferenceInfluencedScopeSet;
	}

	private static Scope getEffortInferenceBaseScope(final ProjectContext context, final ModelAction action) {
		final Scope s = context.findScope(action.getReferenceId());
		final Scope scope = s.isRoot() || (action instanceof ScopeInsertParentRollbackAction) ? s : s.getParent();
		return scope;
	}
}
