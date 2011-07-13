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
		for (final Scope child : scope.getChildren()) {
			if (recursionIndex > 0) preProcessBottomUp(child, recursionIndex);

			final Effort childEffort = child.getEffort();
			sum += childEffort.getDeclared() > childEffort.getBottomUpValue() ? childEffort.getDeclared() : childEffort.getBottomUpValue();
		}
		scope.getEffort().setBottomUpValue(sum);
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

		if (effort.hasDeclared()) effort.setTopDownValue((effort.getDeclared() > effort.getBottomUpValue()) ? effort.getDeclared() : effort.getBottomUpValue());
		else if (scope.isRoot()) effort.setTopDownValue(0);

		float available = effort.getTopDownValue() - getDeclaredEffortSum(scope);
		if (available < 0) available = 0;

		final List<Scope> childrenWithNonDeclaredEfforts = getChildrenWithNonDeclaredEfforts(scope);
		final float portion = getPortion(available, childrenWithNonDeclaredEfforts);

		final List<Scope> childrenList = scope.getChildren();
		for (final Scope child : childrenList) {
			final Effort childEffort = child.getEffort();
			final boolean childHasDeclared = childEffort.hasDeclared();
			final float value = childHasDeclared && effort.getDeclared() > effort.getBottomUpValue() ? childEffort.getDeclared() : childEffort
					.getBottomUpValue();

			final float initialTopDownValue = childEffort.getTopDownValue();
			if (value > portion) childEffort.setTopDownValue(value);
			else childEffort.setTopDownValue(childHasDeclared ? childEffort.getDeclared() : portion);
			if (childEffort.getTopDownValue() != initialTopDownValue) processTopDown(child);
		}
	}

	private static int getDeclaredEffortSum(final Scope scope) {
		int sum = 0;
		final List<Scope> children = scope.getChildren();
		for (final Scope child : children)
			if (child.getEffort().hasDeclared()) sum += child.getEffort().getDeclared();

		return sum;
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

	private static List<Scope> getChildrenWithNonDeclaredEfforts(final Scope scope) {
		final List<Scope> childrenWithNonDeclaredEfforts = new ArrayList<Scope>();
		for (final Scope child : scope.getChildren())
			if (!child.getEffort().hasDeclared()) childrenWithNonDeclaredEfforts.add(child);

		return childrenWithNonDeclaredEfforts;
	}
}
