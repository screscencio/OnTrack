package br.com.oncast.ontrack.shared.model.scope.inference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class EffortInferenceEngine implements InferenceEngine {

	@Override
	public boolean shouldProcess(final ModelAction action) {
		return action.changesEffortInference();
	}

	@Override
	public Set<UUID> process(final Scope scope) {
		final Set<UUID> inferenceInfluencedScopeSet = new HashSet<UUID>();

		preProcessBottomUp(scope, 2, inferenceInfluencedScopeSet);
		processTopDown(processBottomUp(scope, inferenceInfluencedScopeSet), inferenceInfluencedScopeSet);
		processTopDown(scope, inferenceInfluencedScopeSet);

		return inferenceInfluencedScopeSet;
	}

	private static void preProcessBottomUp(final Scope scope, int recursionIndex, final Set<UUID> inferenceInfluencedScopeSet) {
		recursionIndex -= 1;
		int sum = 0;

		final Effort effort = scope.getEffort();
		boolean hasStronglyDefinedChildren = scope.getChildren().size() > 0;
		for (final Scope child : scope.getChildren()) {
			if (recursionIndex > 0) preProcessBottomUp(child, recursionIndex, inferenceInfluencedScopeSet);

			final Effort childEffort = child.getEffort();
			sum += childEffort.getDeclared() > childEffort.getBottomUpValue() ? childEffort.getDeclared() : childEffort.getBottomUpValue();
			hasStronglyDefinedChildren &= childEffort.isStronglyDefined();
		}
		effort.setBottomUpValue(sum);
		effort.setHasStronglyDefinedChildren(hasStronglyDefinedChildren);
		inferenceInfluencedScopeSet.add(scope.getId());
	}

	private static Scope processBottomUp(final Scope scope, final Set<UUID> inferenceInfluencedScopeSet) {
		if (scope.isRoot()) return scope;
		final Scope parent = scope.getParent();
		final Effort parentEffort = parent.getEffort();

		final float inferedInitial = parentEffort.getInfered();
		preProcessBottomUp(parent, 0, inferenceInfluencedScopeSet);
		final float inferedFinal = parentEffort.getInfered();

		if (inferedFinal == inferedInitial) return parent;
		return processBottomUp(parent, inferenceInfluencedScopeSet);
	}

	private static void processTopDown(final Scope scope, final Set<UUID> inferenceInfluencedScopeSet) {
		final Effort effort = scope.getEffort();

		if (effort.isStronglyDefined()) effort.setTopDownValue((effort.getDeclared() > effort.getBottomUpValue()) ? effort.getDeclared() : effort
				.getBottomUpValue());
		else if (scope.isRoot()) effort.setTopDownValue(getStronglyDefinedEffortSum(scope));

		float available = effort.getTopDownValue() - getStronglyDefinedEffortSum(scope);
		if (available < 0) available = 0;

		final List<Scope> childrenWithNonDeclaredEfforts = getChildrenWithNonStronglyDefinedEfforts(scope);
		final float portion = getPortion(available, childrenWithNonDeclaredEfforts);

		final List<Scope> childrenList = scope.getChildren();
		for (final Scope child : childrenList) {
			final Effort childEffort = child.getEffort();
			final boolean isChildStronglyDefined = childEffort.isStronglyDefined();
			final float value = isChildStronglyDefined && effort.getDeclared() > effort.getBottomUpValue() ? childEffort.getDeclared() : childEffort
					.getBottomUpValue();

			final float initialTopDownValue = childEffort.getTopDownValue();
			if (value > portion) childEffort.setTopDownValue(value);
			else childEffort.setTopDownValue(isChildStronglyDefined ? childEffort.getDeclared() : portion);

			if (childEffort.getTopDownValue() != initialTopDownValue) {
				inferenceInfluencedScopeSet.add(child.getId());
				processTopDown(child, inferenceInfluencedScopeSet);
			}
		}
	}

	private static int getStronglyDefinedEffortSum(final Scope scope) {
		int sum = 0;
		final List<Scope> children = scope.getChildren();
		for (final Scope child : children)
			if (child.getEffort().isStronglyDefined()) sum += (child.getEffort().getDeclared() > child.getEffort().getBottomUpValue()) ? child.getEffort()
					.getDeclared() : child.getEffort().getBottomUpValue();

		return sum;
	}

	private static List<Scope> getChildrenWithNonStronglyDefinedEfforts(final Scope scope) {
		final List<Scope> childrenWithNonStronglyDefinedEfforts = new ArrayList<Scope>();
		for (final Scope child : scope.getChildren())
			if (!child.getEffort().isStronglyDefined()) childrenWithNonStronglyDefinedEfforts.add(child);

		return childrenWithNonStronglyDefinedEfforts;
	}

	private static float getPortion(final float available, final List<Scope> scopeList) {
		if (available <= 0) return 0;
		if (scopeList.size() == 0) return 0;

		final float portion = available / scopeList.size();
		for (final Scope scope : scopeList) {
			final float bottomUpValue = scope.getEffort().getBottomUpValue();
			if (bottomUpValue > portion) {
				final ArrayList<Scope> list = new ArrayList<Scope>(scopeList);
				list.remove(scope);
				return getPortion(available - bottomUpValue, list);
			}
		}

		return portion;
	}
}