package br.com.oncast.ontrack.shared.model.effort;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.scope.Scope;

public class EffortInferenceEngine {

	public static void process(final Scope scope) {
		preProcessBottomUp(scope, 2);
		processTopDown(processBottomUp(scope));
		processTopDown(scope);
	}

	private static void preProcessBottomUp(final Scope scope, int recursionIndex) {
		recursionIndex -= 1;
		int sum = 0;

		final Effort effort = scope.getEffort();
		boolean hasStronglyDefinedChildren = scope.getChildren().size() > 0;
		for (final Scope child : scope.getChildren()) {
			if (recursionIndex > 0) preProcessBottomUp(child, recursionIndex);

			final Effort childEffort = child.getEffort();
			sum += childEffort.getDeclared() > childEffort.getBottomUpValue() ? childEffort.getDeclared() : childEffort.getBottomUpValue();
			hasStronglyDefinedChildren &= childEffort.isStronglyDefined();
		}
		effort.setBottomUpValue(sum);
		effort.setHasStronglyDefinedChildren(hasStronglyDefinedChildren);
	}

	private static Scope processBottomUp(final Scope scope) {
		if (scope.isRoot()) return scope;
		final Scope parent = scope.getParent();
		final Effort parentEffort = parent.getEffort();

		final float inferedInitial = parentEffort.getInfered();
		preProcessBottomUp(parent, 0);
		final float inferedFinal = parentEffort.getInfered();

		if (inferedFinal == inferedInitial) return parent;
		return processBottomUp(parent);
	}

	private static void processTopDown(final Scope scope) {
		final Effort effort = scope.getEffort();

		if (effort.isStronglyDefined()) effort.setTopDownValue((effort.getDeclared() > effort.getBottomUpValue()) ? effort.getDeclared() : effort
				.getBottomUpValue());
		else if (scope.isRoot()) getStronglyDefinedEffortSum(scope);

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
			if (childEffort.getTopDownValue() != initialTopDownValue) processTopDown(child);
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
