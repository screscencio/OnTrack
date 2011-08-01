package br.com.oncast.ontrack.shared.model.scope.inference;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.scope.Scope;
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
			checkTopDownDistribution(child, inferenceInfluencedScopeSet);

		for (final Scope child : children)
			calculateBottomUp(child, inferenceInfluencedScopeSet);

		processBottomUp(scope, inferenceInfluencedScopeSet);

		return inferenceInfluencedScopeSet;
	}

	private void checkTopDownDistribution(final Scope scope, final HashSet<UUID> inferenceInfluencedScopeSet) {
		if (scope.isLeaf() || !scope.getProgress().hasDeclared()) return;
		processTopDownDistribution(scope, scope.getProgress().getDescription(), inferenceInfluencedScopeSet);
	}

	private void processTopDownDistribution(final Scope scope, final String progressDescription, final HashSet<UUID> inferenceInfluencedScopeSet) {
		final Progress progress = scope.getProgress();

		if (scope.isLeaf()) {
			progress.setDescription(progressDescription);
			inferenceInfluencedScopeSet.add(scope.getId());
		}
		else {
			for (final Scope child : scope.getChildren())
				processTopDownDistribution(child, progressDescription, inferenceInfluencedScopeSet);
			progress.reset();
			calculateBottomUp(scope, inferenceInfluencedScopeSet);
		}
	}

	private void processBottomUp(final Scope scope, final HashSet<UUID> inferenceInfluencedScopeSet) {
		calculateBottomUp(scope, inferenceInfluencedScopeSet);

		if (scope.isRoot()) return;
		processBottomUp(scope.getParent(), inferenceInfluencedScopeSet);
	}

	private void calculateBottomUp(final Scope scope, final HashSet<UUID> inferenceInfluencedScopeSet) {
		final float computedEffort = scope.getProgress().isDone() ? scope.getEffort().getInfered() : calculateComputedEffort(scope);
		scope.getEffort().setComputedEffort(computedEffort);
		inferenceInfluencedScopeSet.add(scope.getId());
	}

	private float calculateComputedEffort(final Scope scope) {
		float doneSum = 0;

		final List<Scope> children = scope.getChildren();
		for (final Scope child : children)
			doneSum += child.getEffort().getComputedEffort();

		return doneSum;
	}
}
