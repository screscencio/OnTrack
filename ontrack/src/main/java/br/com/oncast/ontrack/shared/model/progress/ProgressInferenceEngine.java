/*
 * DECISION [02/08/2011] As suggested by Rodrigo, it was decided to not use "damage-control" because the damage control implementation we had would only
 * take in account the action modification and not the modifications done by other inference engines (like EffortInferenceEngine).
 */
// DECISION [02/08/2011] Progress distribution was removed to simplify the ProgressInferenceEngine and its architecture: CTRL+Z should be thought over.

package br.com.oncast.ontrack.shared.model.progress;

import static br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState.DONE;
import static br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState.NOT_STARTED;
import static br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState.UNDER_WORK;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.inference.InferenceOverScopeEngine;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
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
	public Set<UUID> process(final Scope scope, final UserRepresentation author, final Date timestamp) {
		final HashSet<UUID> updatedScopes = new HashSet<UUID>();

		if (scope.isLeaf()) setState(scope, NOT_STARTED, updatedScopes, author, timestamp);
		propagateToDescendants(scope, updatedScopes, author, timestamp);
		propagateToAncestors(scope, updatedScopes, author, timestamp);

		processBottomUpAccomplishedEffort(getRoot(scope), updatedScopes);

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
		ProgressState state = DONE;
		for (final Scope sibling : parent.getChildren()) {
			if (state.equals(UNDER_WORK) || is(sibling, UNDER_WORK)) {
				state = UNDER_WORK;
			}
			else if (is(sibling, NOT_STARTED)) state = NOT_STARTED;
		}

		if (setState(parent, state, updatedScopes, author, timestamp)) propagateToAncestors(parent, updatedScopes, author, timestamp);
	}

	private void propagateToDescendants(final Scope scope, final HashSet<UUID> updatedScopes, final UserRepresentation author, final Date timestamp) {
		if (scope.isLeaf()) return;

		ProgressState state = scope.getProgress().getState();
		if (!state.equals(DONE)) state = NOT_STARTED;

		boolean allDone = true;

		for (final Scope child : scope.getChildren()) {
			if (setState(child, state, updatedScopes, author, timestamp)) propagateToDescendants(child, updatedScopes, author, timestamp);
			allDone &= is(child, DONE);
		}

		if (!scope.isLeaf() && is(scope, NOT_STARTED) && allDone) setState(scope, DONE, updatedScopes, author, timestamp);
	}

	private boolean setState(final Scope scope, final ProgressState newState, final HashSet<UUID> updatedScopes, final UserRepresentation author,
			final Date timestamp) {
		final Progress progress = scope.getProgress();
		final ProgressState previousState = progress.getState();
		if (newState.equals(NOT_STARTED)) progress.updateStateToDeclared(author, timestamp);
		else progress.setState(newState, author, timestamp);

		final boolean updated = !is(scope, previousState);
		if (updated) updatedScopes.add(scope.getId());
		return updated;
	}

	private boolean is(final Scope scope, final ProgressState state) {
		return scope.getProgress().getState().equals(state);
	}

	private void processBottomUpAccomplishedEffort(final Scope scope, final HashSet<UUID> updatedScopes) {
		for (final Scope child : scope.getChildren())
			processBottomUpAccomplishedEffort(child, updatedScopes);

		calculateBottomUpEffort(scope, updatedScopes);
	}

	private void calculateBottomUpEffort(final Scope scope, final HashSet<UUID> updatedScopes) {
		final float newAccomplishedEffort = is(scope, DONE) ? scope.getEffort().getInfered() : calculateAccomplishedEffort(scope);

		if (Math.abs(newAccomplishedEffort - scope.getEffort().getAccomplished()) > EPSILON) {
			scope.getEffort().setAccomplished(newAccomplishedEffort);
			updatedScopes.add(scope.getId());
		}
	}

	private float calculateAccomplishedEffort(final Scope scope) {
		float doneSum = 0;

		for (final Scope child : scope.getChildren())
			doneSum += child.getEffort().getAccomplished();

		return doneSum;
	}

}
