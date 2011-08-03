/*
 * Decisions:
 * - [02/08/2011] As suggested by Rodrigo, it was decided to not use "damage-control" because the damage control implementation we had would only
 * take in account the action modification and not the modifications done by other inference engines (like EffortInferenceEngine).
 * - [02/08/2011] Progress distribution was removed to simplify the ProgressInferenceEngine and its architecture: CTRL+Z should be thought over.
 */

package br.com.oncast.ontrack.shared.model.progress;

import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.inference.InferenceOverScopeEngine;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

// TODO Possible optimization may be necessary as this algorithm does not make use of "damage-control", because the damage control implementation we had would
// only take in account the action modification and not the modifications done by other inference engines (like EffortInferenceEngine).
public class ProgressInferenceEngine implements InferenceOverScopeEngine {

	private static final float EPSILON = 0.01f;

	@Override
	public boolean shouldProcess(final ModelAction action) {
		return action.changesProcessInference();
	}

	@Override
	public Set<UUID> process(final Scope scope) {
		final HashSet<UUID> inferenceInfluencedScopeSet = new HashSet<UUID>();
		processBottomUp(getRoot(scope), inferenceInfluencedScopeSet);

		return inferenceInfluencedScopeSet;
	}

	private Scope getRoot(Scope scope) {
		while (!scope.isRoot())
			scope = scope.getParent();
		return scope;
	}

	private void processBottomUp(final Scope scope, final HashSet<UUID> inferenceInfluencedScopeSet) {
		for (final Scope child : scope.getChildren())
			processBottomUp(child, inferenceInfluencedScopeSet);

		calculateBottomUp(scope, inferenceInfluencedScopeSet);
	}

	private void calculateBottomUp(final Scope scope, final HashSet<UUID> inferenceInfluencedScopeSet) {
		boolean shouldBeInsertedIntoSet = false;
		final Progress progress = scope.getProgress();

		if (scope.isLeaf()) {
			if (!progress.hasDeclared() && progress.isDone()) {
				progress.setState(ProgressState.NOT_STARTED);
				shouldBeInsertedIntoSet = true;
			}
		}
		else {
			if (!progress.getDescription().isEmpty()) {
				progress.setDescription("");
				shouldBeInsertedIntoSet = true;
			}

			if (shouldProgressBeMarketAsCompleted(scope)) {
				if (!progress.isDone()) {
					progress.setState(ProgressState.DONE);
					shouldBeInsertedIntoSet = true;
				}
			}
			else {
				if (!progress.hasDeclared() && progress.isDone()) {
					progress.setState(ProgressState.NOT_STARTED);
					shouldBeInsertedIntoSet = true;
				}
			}
		}

		final float newAccomplishedEffort = progress.isDone() ? scope.getEffort().getInfered() : calculateAccomplishedEffort(scope);

		if (Math.abs(newAccomplishedEffort - scope.getEffort().getAccomplishedEffort()) > EPSILON) {
			scope.getEffort().setAccomplishedEffort(newAccomplishedEffort);
			shouldBeInsertedIntoSet = true;
		}

		if (shouldBeInsertedIntoSet) inferenceInfluencedScopeSet.add(scope.getId());
	}

	private boolean shouldProgressBeMarketAsCompleted(final Scope scope) {
		if (scope.isLeaf()) return scope.getProgress().isDone();
		for (final Scope child : scope.getChildren())
			if (!child.getProgress().isDone()) return false;
		return true;
	}

	private float calculateAccomplishedEffort(final Scope scope) {
		float doneSum = 0;

		for (final Scope child : scope.getChildren())
			doneSum += child.getEffort().getAccomplishedEffort();

		return doneSum;
	}
}
