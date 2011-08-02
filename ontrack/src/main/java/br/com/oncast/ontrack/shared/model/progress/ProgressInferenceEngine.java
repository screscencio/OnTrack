package br.com.oncast.ontrack.shared.model.progress;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.inference.InferenceOverScopeEngine;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ProgressInferenceEngine implements InferenceOverScopeEngine {

	@Override
	public boolean shouldProcess(final ModelAction action) {
		return action.changesProcessInference();
	}

	@Override
	public Set<UUID> process(final Scope scope) {
		final HashSet<UUID> inferenceInfluencedScopeSet = new HashSet<UUID>();
		final List<Scope> children = scope.getChildren();

		if (!checkTopDownDistribution(scope, inferenceInfluencedScopeSet)) {
			for (final Scope child : children)
				checkTopDownDistribution(child, inferenceInfluencedScopeSet);
		}

		for (final Scope child : children)
			calculateBottomUp(child, inferenceInfluencedScopeSet);

		processBottomUp(scope, inferenceInfluencedScopeSet);

		return inferenceInfluencedScopeSet;
	}

	private boolean checkTopDownDistribution(final Scope scope, final HashSet<UUID> inferenceInfluencedScopeSet) {
		if (scope.isLeaf() || !scope.getProgress().hasDeclared()) return false;
		processTopDownDistribution(scope, scope.getProgress().getDescription(), inferenceInfluencedScopeSet);
		return true;
	}

	private void processTopDownDistribution(final Scope scope, final String progressDescription, final HashSet<UUID> inferenceInfluencedScopeSet) {
		if (scope.isLeaf()) {
			scope.getProgress().setDescription(progressDescription);
			inferenceInfluencedScopeSet.add(scope.getId());
		}
		else {
			for (final Scope child : scope.getChildren())
				processTopDownDistribution(child, progressDescription, inferenceInfluencedScopeSet);
		}
		calculateBottomUp(scope, inferenceInfluencedScopeSet);
	}

	private void processBottomUp(final Scope scope, final HashSet<UUID> inferenceInfluencedScopeSet) {
		calculateBottomUp(scope, inferenceInfluencedScopeSet);

		if (scope.isRoot()) return;
		processBottomUp(scope.getParent(), inferenceInfluencedScopeSet);
	}

	private void calculateBottomUp(final Scope scope, final HashSet<UUID> inferenceInfluencedScopeSet) {
		if (!scope.isLeaf()) {
			scope.getProgress().setDescription("");
			if (determineProgressCompletition(scope)) scope.getProgress().markAsCompleted();
		}
		scope.getEffort().setComputedEffort((scope.getProgress().isDone() ? scope.getEffort().getInfered() : calculateComputedEffort(scope)));
		inferenceInfluencedScopeSet.add(scope.getId());
	}

	private boolean determineProgressCompletition(final Scope scope) {
		if (scope.isLeaf()) return scope.getProgress().isDone();
		for (final Scope child : scope.getChildren())
			if (!child.getProgress().isDone()) return false;
		return true;
	}

	private float calculateComputedEffort(final Scope scope) {
		float doneSum = 0;

		for (final Scope child : scope.getChildren())
			doneSum += child.getEffort().getComputedEffort();

		return doneSum;
	}
}
