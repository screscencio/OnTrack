package br.com.oncast.ontrack.shared.model.effort;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.scope.Scope;

public class EffortInferenceEngine {

	public static void process(final Scope scope) {
		calculateEffort(scope, 3);
		processBottomUp(scope);
		processTopDown(scope);
	}

	private static void processBottomUp(final Scope scope) {
		final float inferedInitial = scope.getEffort().getInfered();
		calculateEffort(scope, 2);
		final float inferedFinal = scope.getEffort().getInfered();

		if (inferedFinal == inferedInitial) return;
		if (scope.isRoot()) return;

		processBottomUp(scope.getParent());
	}

	private static void processTopDown(final Scope scope) {
		final float infered = scope.getEffort().getInfered();
		final float calculated = scope.getEffort().getCalculated();
		final float difference = infered - calculated;

		if (difference == 0) return;
		if (difference < 0) throw new RuntimeException("Error while infering efforts.");

		final List<Scope> childrenWithNonDeclaredEfforts = getChildrenWithNonDeclaredEfforts(scope);
		final int size = childrenWithNonDeclaredEfforts.size();
		if (size == 0) return;

		final float portion = difference / size;
		for (final Scope child : childrenWithNonDeclaredEfforts) {
			// TODO Review the method of calculation - could pre calculate it once using the (infered - sumOfDeclaratedChildren / size)
			child.getEffort().setCalculated(child.getEffort().getCalculated() + portion);
			processTopDown(child);
		}
	}

	private static List<Scope> getChildrenWithNonDeclaredEfforts(final Scope scope) {
		final List<Scope> childrenWithNonDeclaredEfforts = new ArrayList<Scope>();
		for (final Scope child : scope.getChildren())
			if (!child.getEffort().hasDeclared()) childrenWithNonDeclaredEfforts.add(child);

		return childrenWithNonDeclaredEfforts;
	}

	private static int calculateEffort(final Scope scope, int i) {
		i -= 1;
		int sum = 0;
		for (final Scope child : scope.getChildren()) {
			if (i > 0) calculateEffort(child, i);
			sum += child.getEffort().getInfered();
		}
		scope.getEffort().setCalculated(sum);

		return sum;
	}
}
