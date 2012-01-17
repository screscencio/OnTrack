package br.com.oncast.ontrack.shared.services.actionExecution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertParentRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveRollbackAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.progress.ProgressInferenceEngine;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.inference.InferenceOverScopeEngine;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.model.value.ValueInferenceEngine;

public class ScopeActionExecuter implements ModelActionExecuter {

	private static final List<InferenceOverScopeEngine> inferenceEngines = new ArrayList<InferenceOverScopeEngine>();

	static {
		inferenceEngines.add(new EffortInferenceEngine());
		inferenceEngines.add(new ValueInferenceEngine());
		inferenceEngines.add(new ProgressInferenceEngine());
	}

	public ScopeActionExecuter() {
		// Constructor must be package visible.
	}

	@Override
	public ActionExecutionContext executeAction(final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		Scope scope;
		try {
			scope = getInferenceBaseScope(context, action);
		}
		catch (final ScopeNotFoundException e) {
			throw new UnableToCompleteActionException(e);
		}

		final ModelAction reverseAction = action.execute(context);
		final Set<UUID> inferenceInfluencedScopeSet = executeInferenceEngines((ScopeAction) action, scope);

		return new ActionExecutionContext(reverseAction, inferenceInfluencedScopeSet);
	}

	protected static Set<UUID> executeInferenceEngines(final ScopeAction action, final Scope scope) {
		final Set<UUID> inferenceInfluencedScopeSet = new HashSet<UUID>();
		for (final InferenceOverScopeEngine inferenceEngine : inferenceEngines)
			if (inferenceEngine.shouldProcess(action)) inferenceInfluencedScopeSet.addAll(inferenceEngine.process(scope));
		return inferenceInfluencedScopeSet;
	}

	protected static Scope getInferenceBaseScope(final ProjectContext context, final ModelAction action) throws ScopeNotFoundException {
		final Scope s = context.findScope(action.getReferenceId());
		final Scope scope = s.isRoot()
				|| (action instanceof ScopeInsertParentRollbackAction)
				|| (action instanceof ScopeInsertChildAction)
				|| (action instanceof ScopeRemoveRollbackAction) ? s : s.getParent();
		return scope;
	}
}
