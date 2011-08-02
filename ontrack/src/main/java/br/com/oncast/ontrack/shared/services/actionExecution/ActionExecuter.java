package br.com.oncast.ontrack.shared.services.actionExecution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.progress.ProgressInferenceEngine;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertParentRollbackAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.inference.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.inference.InferenceOverScopeEngine;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ActionExecuter {

	private static final List<InferenceOverScopeEngine> inferenceEngines = new ArrayList<InferenceOverScopeEngine>();

	static {
		inferenceEngines.add(new EffortInferenceEngine());
		inferenceEngines.add(new ProgressInferenceEngine());
	}

	public static ActionExecutionContext executeAction(final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		final Scope scope = getEffortInferenceBaseScope(context, action);

		final ModelAction reverseAction = action.execute(context);
		final Set<UUID> inferenceInfluencedScopeSet = executeInferenceEngines(action, scope);

		return new ActionExecutionContext(reverseAction, inferenceInfluencedScopeSet);
	}

	private static Set<UUID> executeInferenceEngines(final ModelAction action, final Scope scope) {
		final Set<UUID> inferenceInfluencedScopeSet = new HashSet<UUID>();
		for (final InferenceOverScopeEngine inferenceEngine : inferenceEngines)
			if (inferenceEngine.shouldProcess(action)) inferenceInfluencedScopeSet.addAll(inferenceEngine.process(scope));
		return inferenceInfluencedScopeSet;
	}

	private static Scope getEffortInferenceBaseScope(final ProjectContext context, final ModelAction action) {
		final Scope s = context.findScope(action.getReferenceId());
		final Scope scope = s.isRoot() || (action instanceof ScopeInsertParentRollbackAction) ? s : s.getParent();
		return scope;
	}
}
