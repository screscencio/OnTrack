/*
 * DECISION [02/08/2011] As suggested by Rodrigo, it was decided to not use "damage-control" because the damage control implementation we had would only
 * take in account the action modification and not the modifications done by other inference engines (like EffortInferenceEngine).
 */
// DECISION [02/08/2011] Progress distribution was removed to simplify the ProgressInferenceEngine and its architecture: CTRL+Z should be thought over.

package br.com.oncast.ontrack.shared.model.progress;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.inference.InferenceOverScopeEngine;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

// TODO Possible optimization may be necessary as this algorithm does not make use of "damage-control", because the damage control implementation we had would
// only take in account the action modification and not the modifications done by other inference engines (like EffortInferenceEngine).
public class ProgressInferenceEngine implements InferenceOverScopeEngine {

	private static final float EPSILON = 0.01f;

	@Override
	public boolean shouldProcess(final ScopeAction action) {
		return action.changesProgressInference();
	}

	@Override
	public Set<UUID> process(final Scope scope, final User author, final Date timestamp) {
		final HashSet<UUID> inferenceInfluencedScopeSet = new HashSet<UUID>();
		processBottomUp(getRoot(scope), inferenceInfluencedScopeSet, author, timestamp);

		return inferenceInfluencedScopeSet;
	}

	private Scope getRoot(Scope scope) {
		while (!scope.isRoot())
			scope = scope.getParent();
		return scope;
	}

	private void processBottomUp(final Scope scope, final HashSet<UUID> inferenceInfluencedScopeSet, final User author, final Date timestamp) {
		for (final Scope child : scope.getChildren())
			processBottomUp(child, inferenceInfluencedScopeSet, author, timestamp);

		calculateBottomUp(scope, inferenceInfluencedScopeSet, author, timestamp);
	}

	private void calculateBottomUp(final Scope scope, final HashSet<UUID> inferenceInfluencedScopeSet, final User author, final Date timestamp) {
		boolean shouldBeInsertedIntoSet = false;
		final Progress progress = scope.getProgress();

		if (scope.isLeaf()) {
			if (!progress.hasDeclared() && progress.isDone()) {
				progress.setState(ProgressState.NOT_STARTED, author, timestamp);
				shouldBeInsertedIntoSet = true;
			}
		}
		else {
			if (shouldProgressBeMarkedAsUnderWork(scope)) {
				if (ProgressState.UNDER_WORK != progress.getState()) {
					progress.setState(ProgressState.UNDER_WORK, author, timestamp);
					shouldBeInsertedIntoSet = true;
				}
			}
			else if (shouldProgressBeMarketAsCompleted(scope)) {
				if (!progress.isDone()) {
					progress.setState(ProgressState.DONE, author, timestamp);
					shouldBeInsertedIntoSet = true;
				}
			}
			else {
				if (!progress.hasDeclared() && progress.isDone()) {
					progress.setState(ProgressState.NOT_STARTED, author, timestamp);
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

	private boolean shouldProgressBeMarkedAsUnderWork(final Scope scope) {
		assert !scope.isLeaf();

		if (scope.getProgress().hasDeclared()) return ProgressState.UNDER_WORK == scope.getProgress().getState();

		for (final Scope child : scope.getChildren())
			if (ProgressState.UNDER_WORK == child.getProgress().getState()) return true;

		return false;
	}

	private boolean shouldProgressBeMarketAsCompleted(final Scope scope) {
		assert !scope.isLeaf();

		if (scope.getProgress().isDone() && scope.getProgress().hasDeclared()) return true;

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
