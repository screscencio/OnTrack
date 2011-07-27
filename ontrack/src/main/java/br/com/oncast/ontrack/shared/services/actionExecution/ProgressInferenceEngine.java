package br.com.oncast.ontrack.shared.services.actionExecution;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.inference.InferenceEngine;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ProgressInferenceEngine implements InferenceEngine {

	@Override
	public boolean shouldProcess(final ModelAction action) {
		return action.changesProcessInference();
	}

	@Override
	public Set<UUID> process(final Scope scope) {
		final HashSet<UUID> inferenceInfluencedScopeSet = new HashSet<UUID>();

		final List<Scope> children = scope.getChildren();
		for (final Scope child : children)
			preProcessBottomUp(child, inferenceInfluencedScopeSet);
		processBottomUp(scope, inferenceInfluencedScopeSet);

		return inferenceInfluencedScopeSet;
	}

	private void processBottomUp(final Scope scope, final HashSet<UUID> inferenceInfluencedScopeSet) {
		preProcessBottomUp(scope, inferenceInfluencedScopeSet);
		processBottomUp(scope.getParent(), inferenceInfluencedScopeSet);
	}

	private void preProcessBottomUp(final Scope scope, final HashSet<UUID> inferenceInfluencedScopeSet) {
		final float computedEffort = scope.getProgress().isDone() ? scope.getEffort().getInfered() : calculateComputedEffort(scope);
		scope.getProgress().setComputedEffort(computedEffort);
		inferenceInfluencedScopeSet.add(scope.getId());

		if (scope.isRoot()) return;
	}

	private float calculateComputedEffort(final Scope scope) {
		float doneSum = 0;

		final List<Scope> children = scope.getChildren();
		for (final Scope child : children)
			doneSum += child.getProgress().getComputedEffort();

		return doneSum;
	}
}
