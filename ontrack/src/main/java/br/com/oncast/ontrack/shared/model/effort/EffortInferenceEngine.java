package br.com.oncast.ontrack.shared.model.effort;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.scope.Scope;

public class EffortInferenceEngine {

	private static int opCount = 0;

	public static void process(final Scope scope) {
		preProcessBottomUp(scope, 2);
		final Scope topMostProcessedScope = processBottomUp(scope);
		processTopDown(topMostProcessedScope);
		processTopDown(scope);
	}

	private static void preProcessBottomUp(final Scope scope, int i) {
		i -= 1;
		int sum = 0;
		for (final Scope child : scope.getChildren()) {
			if (i > 0) preProcessBottomUp(child, i);

			final Effort childEffort = child.getEffort();
			sum += childEffort.getDeclared() > childEffort.getBottomUpValue() ? childEffort.getDeclared() : childEffort.getBottomUpValue();
		}
		scope.getEffort().setBottomUpValue(sum);
		log("preProcessBottomUp", scope);
	}

	private static void log(final String descrip, final Scope scope) {
		System.out.println("Operation " + opCount++ + ": \t" + descrip + "\t - \t" + scope.getDescription());
	}

	private static Scope processBottomUp(final Scope scope) {
		log("processBottomUp", scope);
		if (scope.isRoot()) return scope;
		final Scope parent = scope.getParent();

		final float inferedInitial = parent.getEffort().getInfered();
		preProcessBottomUp(parent, 0);
		final float inferedFinal = parent.getEffort().getInfered();

		if (inferedFinal == inferedInitial) return parent;
		return processBottomUp(parent);
	}

	private static boolean processTopDown(final Scope scope) {
		log("processTopDown", scope);
		if (scope.getEffort().hasDeclared()) scope.getEffort().setTopDownValue(scope.getEffort().getDeclared());

		float available = scope.getEffort().getTopDownValue() - getDeclaredEffortSum(scope);
		if (available < 0) available = 0;

		final List<Scope> childrenWithNonDeclaredEfforts = getChildrenWithNonDeclaredEfforts(scope);
		final float portion = getPortion(available, childrenWithNonDeclaredEfforts);

		boolean processTopDown = false;

		final List<Scope> childrenList = scope.getChildren();
		for (final Scope child : childrenList) {
			final float initialTopDownValue = child.getEffort().getTopDownValue();

			final float value = (child.getEffort().hasDeclared()) ? child.getEffort().getDeclared() : child.getEffort().getBottomUpValue();
			if (value > portion) child.getEffort().setTopDownValue(value);
			else child.getEffort().setTopDownValue(child.getEffort().hasDeclared() ? child.getEffort().getDeclared() : portion);

			if (child.getEffort().getTopDownValue() != initialTopDownValue) processTopDown |= processTopDown(child);
		}

		// if (processTopDown) preProcessBottomUp(scope, 0);
		return processTopDown;
	}

	private static int getDeclaredEffortSum(final Scope scope) {
		int sum = 0;
		final List<Scope> children = scope.getChildren();
		for (final Scope child : children)
			if (child.getEffort().hasDeclared()) sum += child.getEffort().getDeclared();

		return sum;
	}

	private static float getPortion(final float available, final List<Scope> scopeList) {
		if (scopeList.size() == 0) return 0;

		final float portion = available / scopeList.size();
		for (final Scope scope : scopeList) {
			if (scope.getEffort().getBottomUpValue() > portion) {
				final ArrayList<Scope> list = new ArrayList<Scope>(scopeList);
				list.remove(scope);
				return getPortion(available - scope.getEffort().getBottomUpValue(), list);
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
