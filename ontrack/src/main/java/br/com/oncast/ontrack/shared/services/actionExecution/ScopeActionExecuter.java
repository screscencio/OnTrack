package br.com.oncast.ontrack.shared.services.actionExecution;

import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingDownRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingUpRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.action.UserAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.prioritizationCriteria.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.prioritizationCriteria.ValueInferenceEngine;
import br.com.oncast.ontrack.shared.model.progress.ProgressInferenceEngine;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.inference.InferenceOverScopeEngine;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	public ActionExecutionContext executeAction(final ProjectContext context, final UserAction action) throws UnableToCompleteActionException {
		final Scope scope = getInferenceBaseScope(context, action.getModelAction());

		final ModelAction reverseAction = action.execute(context);
		final Set<UUID> inferenceInfluencedScopeSet = executeInferenceEngines((ScopeAction) action.getModelAction(), scope, ActionHelper.findActionAuthor(context, action),
				action.getExecutionTimestamp());

		return new ActionExecutionContext(action, reverseAction, inferenceInfluencedScopeSet);
	}

	protected static Set<UUID> executeInferenceEngines(final ScopeAction action, final Scope scope, final UserRepresentation author, final Date timestamp) {
		final Set<UUID> inferenceInfluencedScopeSet = new HashSet<UUID>();
		for (final InferenceOverScopeEngine inferenceEngine : inferenceEngines)
			if (inferenceEngine.shouldProcess(action)) inferenceInfluencedScopeSet.addAll(inferenceEngine.process(scope, author, timestamp));
		return inferenceInfluencedScopeSet;
	}

	protected static Scope getInferenceBaseScope(final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		final Scope scope = ActionHelper.findScope(action.getReferenceId(), context, action);
		if (!scope.isRoot() && action instanceof ScopeRemoveAction || action instanceof ScopeInsertSiblingDownRollbackAction || action instanceof ScopeInsertSiblingUpRollbackAction
				|| action instanceof ScopeInsertChildRollbackAction) return scope.getParent();
		return scope;
	}
}
