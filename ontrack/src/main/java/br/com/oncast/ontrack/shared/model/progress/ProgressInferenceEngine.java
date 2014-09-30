/*
 * DECISION [02/08/2011] As suggested by Rodrigo, it was decided to not use "damage-control" because the damage control implementation we had would only
 * take in account the action modification and not the modifications done by other inference engines (like EffortInferenceEngine).
 */
// DECISION [02/08/2011] Progress distribution was removed to simplify the ProgressInferenceEngine and its architecture: CTRL+Z should be thought over.

package br.com.oncast.ontrack.shared.model.progress;

import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.inference.InferenceOverScopeEngine;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

// TODO Possible optimization may be necessary as this algorithm does not make use of "damage-control", because the damage control implementation we had would
// only take in account the action modification and not the modifications done by other inference engines (like EffortInferenceEngine).
public class ProgressInferenceEngine implements InferenceOverScopeEngine {

	private static final float EPSILON = 0.01f;

	@Override
	public boolean shouldProcess(final ScopeAction action) {
		return action.changesProgressInference();
	}

	@Override
	public Set<UUID> process(final Scope scope, final UserRepresentation author, final Date timestamp) {
		final HashSet<UUID> updatedScopes = new HashSet<UUID>();

		if (scope.isLeaf()) setState(scope, ProgressState.NOT_STARTED, updatedScopes, author, timestamp);
		propagateToDescendants(scope, updatedScopes, author, timestamp);
		propagateToAncestors(scope, updatedScopes, author, timestamp);

		processBottomUpAccomplishedAmmounts(getRoot(scope), updatedScopes);

		return updatedScopes;
	}

	private Scope getRoot(Scope scope) {
		while (!scope.isRoot())
			scope = scope.getParent();
		return scope;
	}

	private void propagateToAncestors(final Scope scope, final HashSet<UUID> updatedScopes, final UserRepresentation author, final Date timestamp) {
		if (scope.isRoot()) return;

		final Scope parent = scope.getParent();
		ProgressState state = ProgressState.DONE;
		for (final Scope sibling : parent.getChildren()) {
			if (state.equals(ProgressState.UNDER_WORK) || is(sibling, ProgressState.UNDER_WORK)) {
				state = ProgressState.UNDER_WORK;
			} else if (is(sibling, ProgressState.NOT_STARTED)) state = ProgressState.NOT_STARTED;
		}

		if (setState(parent, state, updatedScopes, author, timestamp)) propagateToAncestors(parent, updatedScopes, author, timestamp);
	}

	private void propagateToDescendants(final Scope scope, final HashSet<UUID> updatedScopes, final UserRepresentation author, final Date timestamp) {
		if (scope.isLeaf()) return;

		ProgressState state = scope.getProgress().getState();
		if (!state.equals(ProgressState.DONE)) state = ProgressState.NOT_STARTED;

		boolean allDone = true;

		for (final Scope child : scope.getChildren()) {
			if (setState(child, state, updatedScopes, author, timestamp)) propagateToDescendants(child, updatedScopes, author, timestamp);
			allDone &= is(child, ProgressState.DONE);
		}

		if (!scope.isLeaf() && is(scope, ProgressState.NOT_STARTED) && allDone) setState(scope, ProgressState.DONE, updatedScopes, author, timestamp);
	}

	private boolean setState(final Scope scope, final ProgressState newState, final HashSet<UUID> updatedScopes, final UserRepresentation author, final Date timestamp) {
		final Progress progress = scope.getProgress();
		final ProgressState previousState = progress.getState();
		if (newState.equals(ProgressState.NOT_STARTED)) progress.updateStateToDeclared(author, timestamp);
		else progress.setState(newState, author, timestamp);

		final boolean updated = !is(scope, previousState);
		if (updated) updatedScopes.add(scope.getId());
		return updated;
	}

	private boolean is(final Scope scope, final ProgressState state) {
		return scope.getProgress().getState().equals(state);
	}

	private void processBottomUpAccomplishedAmmounts(final Scope scope, final HashSet<UUID> updatedScopes) {
		for (final Scope child : scope.getChildren())
			processBottomUpAccomplishedAmmounts(child, updatedScopes);

		calculateBottomUpAmmounts(scope, updatedScopes);
	}

	private void calculateBottomUpAmmounts(final Scope scope, final HashSet<UUID> updatedScopes) {
		final float newAccomplishedEffort = is(scope, ProgressState.DONE) ? scope.getEffort().getInfered() : calculateAccomplishedEffort(scope);
		final float newAccomplishedValue = is(scope, ProgressState.DONE) ? scope.getValue().getInfered() : calculateAccomplishedValue(scope);

		if (Math.abs(newAccomplishedEffort - scope.getEffort().getAccomplished()) > EPSILON) {
			scope.getEffort().setAccomplished(newAccomplishedEffort);
			updatedScopes.add(scope.getId());
		}

		if (Math.abs(newAccomplishedValue - scope.getValue().getAccomplished()) > EPSILON) {
			scope.getValue().setAccomplished(newAccomplishedValue);
			updatedScopes.add(scope.getId());
		}
	}

	private float calculateAccomplishedEffort(final Scope scope) {
		float doneSum = 0;

		for (final Scope child : scope.getChildren())
			doneSum += child.getEffort().getAccomplished();

		return doneSum;
	}

	private float calculateAccomplishedValue(final Scope scope) {
		float doneSum = 0;

		for (final Scope child : scope.getChildren())
			doneSum += child.getValue().getAccomplished();

		return doneSum;
	}

}
